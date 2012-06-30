package teambots.smartphone.usbInterface;

import java.util.concurrent.ThreadFactory;

public class CommunicationThreadFactory implements ThreadFactory {

    private String threadName;
    private int priority;
    private long id = 0;

    CommunicationThreadFactory(String threadNames, int priority) {
        this.threadName = threadNames;
        this.priority = priority;
    }
	
	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, threadName + " - " + id);
		t.setDaemon(true);
		
		t.setPriority(priority);
		id++;
		return t;
	}

}
