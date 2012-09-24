package dataLogger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import teambotData.Data;
import teambotData.LoggerInfo;

public class BufferedLogger implements IDataLogger {

	protected String loggerName = "Unspecified BufferedLogger";
	
	public AtomicBoolean running = new AtomicBoolean(true);
	public AtomicBoolean vla;
	protected LoggerStatus status = LoggerStatus.IDLE;
	IMemoryAccess memoryAccess;
	protected int bufferSize; 
	protected LinkedBlockingQueue<Data> dataQueue;
	protected LinkedBlockingQueue<Data> backupQueue;
	
	public BufferedLogger(String loggerName, IMemoryAccess memoryAccess, int bufferSize)
	{	
		new Thread(memoryAccess).start();
		this.loggerName = loggerName;
		this.memoryAccess = memoryAccess;
		
		this.bufferSize = bufferSize;
		dataQueue = new LinkedBlockingQueue<Data>(bufferSize);
		backupQueue = new LinkedBlockingQueue<Data>(bufferSize / 4);
	}

	protected void finalize() throws Throwable 
	{
		memoryAccess.stop();
	}
	
	@Override
	public void run() {
		long timeAtStatusUpdate = 0;
		
		while(running.get())
		{
			if(System.currentTimeMillis() - timeAtStatusUpdate > 100)
			{
				updateStatus();
				timeAtStatusUpdate = System.currentTimeMillis();
			}
			
			if(backupQueue.isEmpty())
				sendData(getDataQueueElement());
			else
			{
				try {
					sendData(backupQueue.take());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	protected void updateStatus()
	{
		float dataQueueUsagePercentage = 1.0f - getDataQueueRemainingCapacity() / (float)bufferSize;
		
		if(dataQueueUsagePercentage < 0.1)
			setStatus(LoggerStatus.IDLE);
		else if(dataQueueUsagePercentage < 0.8)
			setStatus(LoggerStatus.OCCUPIED);
		else if(dataQueueUsagePercentage < 0.9)
			setStatus(LoggerStatus.CRITICAL);
		else
			setStatus(LoggerStatus.AT_LIMIT);
	}
	
	protected synchronized int getDataQueueRemainingCapacity()
	{
		return dataQueue.remainingCapacity();
	}
	
	protected Data getDataQueueElement()
	{
		try {
			return dataQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new LoggerInfo(loggerName + " got InterruptedException while waiting for new data");
	}
	
	void sendData(Data data)
	{
		memoryAccess.save(data);
	}

	//TODO Check if this is thread save -> dataQueue.take() in getDataQueueElement() blocks
	//so synchronizing getDataQueueElement() and log() would give a deadlock?
	@Override
	public void log(Data data) {
		if(this.dataQueue.offer(data))
			return;
			
		this.status = LoggerStatus.AT_LIMIT;
		try {
			this.backupQueue.put(data);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void setStatus(LoggerStatus status){
		this.status = status;
	}

	@Override
	public synchronized LoggerStatus getStatus() {
		return this.status;
	}
}
