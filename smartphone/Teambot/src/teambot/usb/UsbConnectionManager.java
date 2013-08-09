package teambot.usb;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import teambot.common.SimpleEndlessThread;
import teambot.common.interfaces.IUsbIO;
import teambot.common.utils.ThreadUtil;
import tembot.common.SettingsAndroid;

public class UsbConnectionManager extends SimpleEndlessThread implements IUsbCommunication, IUsbIO
{
	protected Context _context;
	protected UsbManager _usbManager;
	protected UsbAccessory _accessory = null;
	protected ParcelFileDescriptor _fileDesciptor;

	protected AtomicBoolean _ioConnected = new AtomicBoolean(false);

	protected FileInputStream _usbInput = null;
	protected FileOutputStream _usbOutput = null;

	public UsbConnectionManager(Context context)
	{
		_context = context;
		_usbManager = (UsbManager) _context.getSystemService(Context.USB_SERVICE);
		
		setupDetach();
	}
	
	protected void setupDetach()
	{
		IntentFilter usbDetachIntentFilter = new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		_context.registerReceiver(_usbDetachtedReceiver, usbDetachIntentFilter);
	}

	BroadcastReceiver _usbDetachtedReceiver = new BroadcastReceiver()
	{
		public void onReceive(Context context, Intent intent)
		{
			String intentAction = intent.getAction();

			if (intentAction.equals(UsbManager.ACTION_USB_ACCESSORY_DETACHED))
			{
				UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
				if (accessory != null && accessory == _accessory)
				{
					closeCommunication();
				}
			}
		}
	};

	protected void doInThreadLoop()
	{
		if (_accessory != null)
		{
			ThreadUtil.sleepSecs(0.5f);
			return;
		}

		UsbAccessory[] accessoryList = _usbManager.getAccessoryList();

		if (accessoryList == null || accessoryList.length == 0 || accessoryList[0] == null)
			return;

		setAccessory(accessoryList[0]);
		setupPermissionRequest();
	}
	
	protected void setupPermissionRequest()
	{
		PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(_context, 0, new Intent(
				SettingsAndroid.ACTION_USB_PERMISSION), 0);
		IntentFilter usbPermissionIntentFilter = new IntentFilter(SettingsAndroid.ACTION_USB_PERMISSION);
		UsbPermissionReceiver usbPermissionReceiver = new UsbPermissionReceiver(this);
		_context.registerReceiver(usbPermissionReceiver, usbPermissionIntentFilter);
		
		_usbManager.requestPermission(_accessory, usbPermissionIntent);
	}

	protected synchronized void setAccessory(UsbAccessory accessory)
	{
		_accessory = accessory;
	}

	@Override
	public synchronized void setupCommunication()
	{
		_fileDesciptor = _usbManager.openAccessory(_accessory);

		if (_fileDesciptor == null)
		{
			_accessory = null;
			return;
		}

		FileDescriptor fd = _fileDesciptor.getFileDescriptor();
		_usbInput = new FileInputStream(fd);
		_usbOutput = new FileOutputStream(fd);

		if (_usbInput != null && _usbOutput != null)
			_ioConnected.set(true);
	}

	@Override
	public synchronized void closeCommunication()
	{
		_ioConnected.set(false);
		ThreadUtil.sleepSecs(1f);

		if (_fileDesciptor != null)
		{
			try
			{
				_fileDesciptor.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public int read(byte[] buffer) throws IOException
	{
		if (!_ioConnected.get())
			throw new IOException("No accessory connected");

		return _usbInput.read(buffer);
	}

	@Override
	public void write(byte[] buffer) throws IOException
	{
		if (!_ioConnected.get())
			throw new IOException("No accessory connected");

		_usbOutput.write(buffer);
	}
}
