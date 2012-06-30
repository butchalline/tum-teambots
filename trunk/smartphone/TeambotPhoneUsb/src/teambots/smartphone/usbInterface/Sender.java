package teambots.smartphone.usbInterface;

import java.io.IOException;
import java.util.Vector;

import android.util.Log;

public class Sender implements Runnable {

	private UsbProxy usbStream;
	private Vector<UsbPackage> highPriorityPackages = new Vector<UsbPackage>(10);
	private Vector<UsbPackage> middlePriorityPackages = new Vector<UsbPackage>(20);
	private Vector<UsbPackage> lowPriorityPackages = new Vector<UsbPackage>(30);
	public boolean activated = true;
	
	public Sender(UsbProxy usbProxy)
	{
		this.usbStream = usbProxy;
	}
	
	public synchronized void queueHighPriorityPackage(UsbPackage usbPackage)
	{
		highPriorityPackages.add(usbPackage);
	}
	
	public synchronized void queueMiddlePriorityPackage(UsbPackage usbPackage)
	{
		middlePriorityPackages.add(usbPackage);
	}
	
	public synchronized void queueLowPriorityPackage(UsbPackage usbPackage)
	{
		lowPriorityPackages.add(usbPackage);
	}
	
	/**
	 * 	Handles the sending of different prioritized packages.
	 * 
	 *  It is guaranteed that each circle a middle and low priority package is send,
	 *  but high priority packages can block the circle in case of a package flooding.
	 *  
	 *  An incoming high priority package stops the processing of the middle and low 
	 *  priority packages. If there are more than 3 middle priority packages in the queue
	 *  the processing of the low priority packages is stopped.
	 *  
	 */
	@Override
	public void run() {
		
		int snapshotOfMiddlePriorityPackageCount;
		int snapshotOfLowPriorityPackageCount;
		
		while(activated)
		{
			guaranteedSending();
			
			snapshotOfMiddlePriorityPackageCount = middlePriorityPackages.size();
			snapshotOfLowPriorityPackageCount = lowPriorityPackages.size();
			
			while (highPriorityPackages.size() > 0)
			{
				send(highPriorityPackages.get(0));
				highPriorityPackages.remove(0);
			}
			while(snapshotOfMiddlePriorityPackageCount > 0 && highPriorityPackages.size() == 0)
			{
				send(middlePriorityPackages.get(0));
				middlePriorityPackages.remove(0);
				snapshotOfMiddlePriorityPackageCount--;
			}
			while(snapshotOfLowPriorityPackageCount > 0 && highPriorityPackages.size() == 0 && middlePriorityPackages.size() < 4)
			{
				send(lowPriorityPackages.get(0));
				lowPriorityPackages.remove(0);
				snapshotOfLowPriorityPackageCount--;
			}
		}		
	}
	
	/**
	 * Is called in each circle to guarantee that at least on middle and
	 * low priority package is send in each circle.
	 */
	private void guaranteedSending()
	{
		if(highPriorityPackages.size() > 0)
		{
			send(highPriorityPackages.get(0));
			highPriorityPackages.remove(0);
		}
		
		if(middlePriorityPackages.size() > 0)
		{
			send(middlePriorityPackages.get(0));
			middlePriorityPackages.remove(0);
		}
		
		if(lowPriorityPackages.size() > 0)
		{
			send(lowPriorityPackages.get(0));
			lowPriorityPackages.remove(0);
		}
	}
	
	private void send(UsbPackage usbPackage)
	{
		try {
			usbStream.write(usbPackage.asDataStream());
		} catch (IOException e) {
			Log.w(UsbInterfaceActivity.TAG, e);
		}
	}

}
