package teambot.common;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import teambot.common.interfaces.IUsbIO;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

public class UsbConnector implements IUsbIO {

	public enum Status {
		ATTACHED, DETACHED, WAITING, PAUSED, ERROR, INITIALIZING
	}

	public interface StatusCallback {
		public void onStatusChange(Status status);
	}

	public Status status = Status.INITIALIZING;

	private static final String TAG = "UsbConnector";
	private static final String ACTION_USB_PERMISSION = "com.google.android.DemoKit.action.USB_PERMISSION";

	private UsbManager mUsbManager;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;
	private Context mContext;
	private StatusCallback mStatusCallback;

	UsbAccessory mAccessory;
	ParcelFileDescriptor mFileDescriptor;
	FileInputStream mInputStream;
	FileOutputStream mOutputStream;

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			synchronized (this) {
				if (ACTION_USB_PERMISSION.equals(action)) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Log.d(TAG, "permission denied for accessory "
								+ accessory);
					}
					mPermissionRequestPending = false;
				} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED
						.equals(action)) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if (accessory != null && accessory.equals(mAccessory)) {
						Log.d(TAG, "received detach intent from system");
						closeAccessory();
					}
				}
			}
		}
	};
	
	private void openAccessory(UsbAccessory accessory) {
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			changeStatus(Status.ATTACHED);
		} else {
			Log.d(TAG,
					"accessory open fail, mFileDescriptor == null aka couldn't get streams");
			changeStatus(Status.ERROR);
			Log.d(TAG,
					"Fuck it, trying to reset the stuff with the resume method..");
			resume();
		}
	}

	private void closeAccessory() {
		Log.d(TAG, "closeAccessory()");
		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
			Log.d(TAG, " IOException when trying to close mFileDescriptor");
		} finally {
			mFileDescriptor = null;
			mAccessory = null;
			Log.d(TAG, "Reset accessory");
			changeStatus(Status.DETACHED);
		}
	}

	public UsbConnector(Context context) {
		initialize(context, null);
	}

	public UsbConnector(Context context, StatusCallback statusCallback) {
		initialize(context, statusCallback);
	}

	private void initialize(Context context, StatusCallback statusCallback) {

		changeStatus(Status.INITIALIZING);
		mContext = context;
		mStatusCallback = statusCallback;
		mUsbManager = UsbManager.getInstance(mContext);
		mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);	
		mContext.registerReceiver(mUsbReceiver, filter);
		Log.d(TAG, "Stuff set up, waiting..");
		changeStatus(Status.WAITING);
	}

	public void resume() {
		Log.d(TAG, "resume()");
		if (mInputStream != null && mOutputStream != null) {
			Log.d(TAG, "Streams initialised so nothing to do here..");
			changeStatus(Status.ATTACHED);
			return;
		}
		
		if(mUsbManager == null)
			mUsbManager = UsbManager.getInstance(mContext);

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					Log.d(TAG,
							"usb manager has not the permission to connect.. ");
					if (!mPermissionRequestPending) {
						Log.d(TAG,
								"no perimission request pending, making a new request");
						mUsbManager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
						return;
					}
					Log.d(TAG,
							".. there is already a permission request pending");
				}
			}
		} else {
			Log.d(TAG, "ERROR: mAccessory is null => forcing a reset..");
			forceReset();
		}
	}

	public void forceReset() {
		Log.d(TAG, "forceReset()");
		changeStatus(Status.INITIALIZING);
		synchronized (mUsbReceiver) {
			closeAccessory();
			Log.d(TAG, "set some stuff null.. ");
			mUsbManager = null;
			mPermissionIntent = null;
			mPermissionRequestPending = false;
			mAccessory = null;
			mFileDescriptor = null;
			mInputStream = null;
			mOutputStream = null;
			Log.d(TAG, ".. done");
			Log.d(TAG, "Ask for a new permission intent");
			mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(
					ACTION_USB_PERMISSION), 0);
			IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
			filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
			try {
				mContext.unregisterReceiver(mUsbReceiver);
			}
			catch (IllegalArgumentException e) {
				Log.d(TAG, "Oops, seems like the BroadcastReceiver wasn't registered at all..");
			}
			mContext.registerReceiver(mUsbReceiver, filter);
			Log.d(TAG, "Stuff reset, waiting..");
			changeStatus(Status.WAITING);
		}
	}

	public void pause() {
		Log.d(TAG, "pause()");
		closeAccessory();
		changeStatus(Status.PAUSED);
	}

	private void changeStatus(Status status) {
		this.status = status;
		Log.d(TAG, "Status changed to " + status);
		if (mStatusCallback != null)
			mStatusCallback.onStatusChange(status);
	}

	protected void finalize() throws Throwable {
		try {
			mContext.unregisterReceiver(mUsbReceiver);
		}
		catch (IllegalArgumentException e) {
		}
		super.finalize();
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		if(mInputStream == null)
			return -1;
		return mInputStream.read(buffer);
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		if(mOutputStream == null)
			return;
		mOutputStream.write(buffer);		
	}
}
