package teambots.smartphone.usbInterface;

import teambot.smartphone.usbInterface.R;
import teambots.smartphone.utilities.RandomStuff;
import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;

public class UsbInterfaceActivity extends Activity {
	
	static final String TAG = "UsbInterface";
	
	UsbProxy proxy;
	Receiver receiver;
	Sender sender;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Debug.startMethodTracing("traceFile");
        //---------------trace-----------------//
        
    	proxy = new MockUsbInterface();
    	receiver = new Receiver(proxy);
  		sender = new Sender(proxy);
    	new Thread(receiver).start();
    	new Thread(sender).start();
    	
    	for(int i = 0; i < 10; i++)
    	{
	    	Message randomMessage = RandomStuff.randomDataMessage(1);
	    	PackageBuilder.threadPool.execute(new HighPriorityPackageBuilderThread(randomMessage, sender));
	    	try {
				Thread.sleep((long)(Math.random() * 1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
        //-------------end trace---------------//
        Debug.stopMethodTracing();
    }
}