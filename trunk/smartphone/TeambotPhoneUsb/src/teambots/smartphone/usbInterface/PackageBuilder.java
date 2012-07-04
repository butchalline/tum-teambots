package teambots.smartphone.usbInterface;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PackageBuilder {

	public static ExecutorService threadPool = Executors.newCachedThreadPool(
			new CommunicationThreadFactory("PackageBuilderThread", Thread.NORM_PRIORITY));
	
	
}
