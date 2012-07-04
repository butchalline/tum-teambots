package teambot.smartphone.usbInterface.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import teambots.smartphone.usbInterface.Message;
import teambots.smartphone.usbInterface.MockReceiverWorkerThread;
import teambots.smartphone.usbInterface.PackageBuilder;
import teambots.smartphone.usbInterface.Receiver;
import teambots.smartphone.usbInterface.Sender;
import teambots.smartphone.usbInterface.UsbProxy;
import teambots.smartphone.utilities.RandomStuff;
import android.util.Log;

public class ReceiverTest extends TestCase {

	UsbProxy proxy;
	Receiver receiver;
	Sender sender;
	
	protected void setUp()
	{
		proxy = new MockUsbInterface();
		receiver = new Receiver(proxy);
		sender = new Sender(proxy);
	}
	
    public void testTrivialSendAndReceive() throws Throwable {
    	new Thread(receiver).start();
    	new Thread(sender).start();
    	Message randomMessage = RandomStuff.randomDataMessage(1);
    	PackageBuilder.threadPool.execute(new HighPriorityPackageBuilderThread(randomMessage, sender));
    	Thread.sleep(2000);
    	
    	Log.d("testTrivialSendAndReceive", "Length rand: " + randomMessage.data.length + "; result length: " + MockReceiverWorkerThread.result.length);
    	Assert.assertTrue(randomMessage.data.length == MockReceiverWorkerThread.result.length);
        for(int i = 0; i < MockReceiverWorkerThread.result.length; i++)    		
    		Assert.assertTrue(randomMessage.data[i] == MockReceiverWorkerThread.result[i]);
        
     }
}

