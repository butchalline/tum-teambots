package teambots.smartphone.usbInterface;

import teambots.smartphone.usbInterface.Message;
import teambots.smartphone.usbInterface.PackageBuilderWorkerThread;
import teambots.smartphone.usbInterface.Sender;
import teambots.smartphone.usbInterface.UsbPackage;

public class HighPriorityPackageBuilderThread extends PackageBuilderWorkerThread {

	public HighPriorityPackageBuilderThread(Message message, Sender sender) {
		super(message, sender);
	}

	@Override
	public void run() {
		UsbPackage usbPackage = new UsbPackage((byte)message.type.id, message.data);
		sender.queueHighPriorityPackage(usbPackage);
	}

}
