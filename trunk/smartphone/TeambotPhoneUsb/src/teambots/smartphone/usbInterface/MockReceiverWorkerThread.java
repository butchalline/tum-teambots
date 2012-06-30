package teambots.smartphone.usbInterface;

import android.util.Log;


public class MockReceiverWorkerThread extends teambots.smartphone.usbInterface.ReceiverWorkerThread {

	public static byte[] result = new byte[0];
	
	public MockReceiverWorkerThread(UsbPackage usbPackage) {
		super(usbPackage);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		
		result = usbPackage.data;
		Log.v("MockReceiver", "Received package, length: " + result.length);
		switch (usbPackage.packageType)
		{
			case Data:
				result = usbPackage.data;
				break;
			default:				
				break;
		}	
	}


}
