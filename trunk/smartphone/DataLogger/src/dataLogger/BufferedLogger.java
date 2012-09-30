package dataLogger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import teambotData.ByteArrayData;
import teambotData.Data;
import teambotData.FloatArrayData;

public class BufferedLogger implements IDataLogger {

	protected String loggerName = "Unspecified BufferedLogger";

	public AtomicBoolean running = new AtomicBoolean(true);
	public AtomicBoolean vla;
	protected LoggerStatus status = LoggerStatus.IDLE;
	IMemoryAccess memoryAccess;
	protected int bufferSize;
	protected LinkedBlockingQueue<Data> dataQueue;
	protected LinkedBlockingQueue<Data> backupQueue;

	public BufferedLogger(String loggerName, IMemoryAccess memoryAccess,
			int bufferSize) {
		new Thread(memoryAccess).start();
		this.loggerName = loggerName;
		this.memoryAccess = memoryAccess;

		this.bufferSize = bufferSize;
		dataQueue = new LinkedBlockingQueue<Data>(bufferSize);
		backupQueue = new LinkedBlockingQueue<Data>(bufferSize / 4);
	}

	protected void finalize() throws Throwable {
		memoryAccess.stop();
	}

	@Override
	public void run() {
		
		while (running.get()) {
			updateStatus();
			if (!backupQueue.isEmpty())
				sendData(backupQueue.poll());
			else {
				try {
					sendData(dataQueue.take());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	protected void updateStatus() {
		float dataQueueUsagePercentage = 1.0f - dataQueue.remainingCapacity()
				/ (float) bufferSize;

		if (dataQueueUsagePercentage < 0.1)
			setStatus(LoggerStatus.IDLE);
		else if (dataQueueUsagePercentage < 0.8)
			setStatus(LoggerStatus.OCCUPIED);
		else if (dataQueueUsagePercentage < 0.9)
			setStatus(LoggerStatus.CRITICAL);
		else
			setStatus(LoggerStatus.AT_LIMIT);
	}

	void sendData(Data data) {
		if (data == null)
			return;

		if (data instanceof FloatArrayData)
			memoryAccess.save((FloatArrayData) data);
		if (data instanceof ByteArrayData)
			memoryAccess.save((ByteArrayData) data);
	}

	// TODO Check if this is thread save -> dataQueue.take() in
	// getDataQueueElement() blocks
	// so synchronizing getDataQueueElement() and log() would give a deadlock?
	@Override
	public boolean log(Data data) {
		if (this.dataQueue.offer(data))
			return true;
		else {
			if(this.backupQueue.offer(data))
				return true;
		}
		return false;
	}

	protected synchronized void setStatus(LoggerStatus status) {
		this.status = status;
	}

	@Override
	public synchronized LoggerStatus getStatus() {
		return this.status;
	}
}
