package teambots.smartphone.usbInterface;

import android.util.Log;

public class ReceiverWorkerThread implements Runnable {

	protected UsbPackage usbPackage;
	
	public ReceiverWorkerThread(UsbPackage usbPackage) {
		
		this.usbPackage = usbPackage;
		Log.v("UsbReceiverThread", "Thread started, package type: " + usbPackage.packageType.name);
	}	
	
	@Override
	public void run() {
		
		switch (usbPackage.packageType)
		{
			case Data:
				break;//throw new UnsupportedOperationException();
			default:
				Log.e("UsbReceiver", "Unknow package received in worker thread, this actually should never happen: \n"
						+ "Package Type: " + usbPackage.packageType.name + "\n"
						+ "Data: " + usbPackage.data);
				break;
		}	
	}

}
