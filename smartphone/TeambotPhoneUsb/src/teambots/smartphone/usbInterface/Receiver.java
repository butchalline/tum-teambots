package teambots.smartphone.usbInterface;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

public class Receiver implements Runnable {

	public static final int MAX_BUFFER_SIZE = 16384;
	public static final int MAX_PACKAGE_SIZE = MAX_BUFFER_SIZE * 2;
	
	private UsbProxy usbStream;
	private PackageExtractor packageExtractor = new PackageExtractor(); 
	private ExecutorService threadPool;
	
	public Receiver(UsbProxy usbProxy)
	{
		this.usbStream = usbProxy;
		threadPool = Executors.newCachedThreadPool(
				new CommunicationThreadFactory("ReceiverThread", Thread.NORM_PRIORITY));
	}
	
	
	@Override
	public void run() {
		
		int numberOfReadBytes = 0;
		byte[] buffer = new byte[MAX_BUFFER_SIZE];
		
		while (numberOfReadBytes >= 0) {
			
			try {				
				numberOfReadBytes = usbStream.read(buffer);
				if(numberOfReadBytes == 0)
					continue;
				packageExtractor.add(numberOfReadBytes, buffer);
			} catch (IOException e) {
				Log.w(UsbInterfaceActivity.TAG, e);
				break;
			}
			
			while(packageExtractor.finishedPackages.size() > 0)
			{				
				threadPool.execute(new MockReceiverWorkerThread(packageExtractor.finishedPackages.get(0)));
				packageExtractor.finishedPackages.remove(0);
			}
				
		}
		
	}
}