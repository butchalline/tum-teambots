package teambot.usb;

import tembot.common.SettingsAndroid;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class UsbPermissionReceiver extends BroadcastReceiver
{
	protected IUsbCommunication _usbCommunication;
	
	public UsbPermissionReceiver(IUsbCommunication usbCommunication)
	{
		super();
		_usbCommunication = usbCommunication;
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String intentAction = intent.getAction();
		if (intentAction.equals(SettingsAndroid.ACTION_USB_PERMISSION))
		{
			synchronized (this)
			{
				UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
				{
					if (accessory != null)
					{
						_usbCommunication.setupCommunication();
					}
				} else
				{
					Log.d("UsbPermissionReceiver", "Permission denied for accessory " + accessory);
				}
			}
		}

	}

}
