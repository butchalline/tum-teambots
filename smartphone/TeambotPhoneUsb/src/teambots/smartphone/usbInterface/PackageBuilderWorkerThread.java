package teambots.smartphone.usbInterface;

import android.util.Log;

public abstract class PackageBuilderWorkerThread implements Runnable {

	protected Message message;
	protected Sender sender;
	
	public PackageBuilderWorkerThread(Message message, Sender sender) {
		this.message = message;
		this.sender = sender;
		Log.v("PackageBuilderThread", "Thread started, package type: " + message.type.name);
	}		
}
