package dataLogger;

import java.util.concurrent.atomic.AtomicBoolean;

import teambotData.Data;

public class LogDistributionManager {
	
	int botId = -1;
	protected IDataLogger networkLogger;
	protected IDataLogger localLogger;
	protected IDataLogger ramMemoryLogger;
	
	protected SaveLocation saveLocation = SaveLocation.NETWORK;
	
	protected LoggerWorkloadWatcher watcher;// = new LoggerWorkloadWatcher();
	
	enum SaveLocation {
		NETWORK,
		LOCAL,
		RAM		
	}
	
	protected class LoggerWorkloadWatcher implements Runnable {

		public AtomicBoolean running = new AtomicBoolean(true);
		
		LoggerStatus networkStatus;
		LoggerStatus diskStatus;
		LoggerStatus memoryStatus;
		
		public LoggerWorkloadWatcher()
		{
			networkStatus = networkLogger.getStatus();
			diskStatus = localLogger.getStatus();
			memoryStatus = ramMemoryLogger.getStatus();
		}
		
		@Override
		public void run() {
			
			while(running.get())
			{
				networkStatus = networkLogger.getStatus();
				
				if (networkStatus != LoggerStatus.CRITICAL && networkStatus != LoggerStatus.AT_LIMIT)
				{
					saveLocation = SaveLocation.NETWORK;
					continue;
				}
				
				if (diskStatus != LoggerStatus.CRITICAL && diskStatus != LoggerStatus.AT_LIMIT)
				{
					saveLocation = SaveLocation.LOCAL;
					continue;
				}
				
				saveLocation = SaveLocation.RAM;
				
				if (memoryStatus == LoggerStatus.CRITICAL)
					networkLogger.log(new teambotData.LoggerInfo(botId, "ramLogger is critical"));
				
				if(memoryStatus == LoggerStatus.AT_LIMIT)
					networkLogger.log(new teambotData.LoggerInfo(botId, "ramLogger is at limit"));
				
				try {
					wait(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	
	
	public LogDistributionManager(int botId, NetworkAccess networkAccess)
	{
		initialize(botId, new BufferedLogger(botId, "NetworkLogger", networkAccess, 100),
				new DisabledDataLogger(),
				new DisabledDataLogger());
	}
	
	public LogDistributionManager(int botId, NetworkAccess networkAccess, IDataLogger localLogger, IDataLogger ramMemoryLogger)
	{
		initialize(botId, new BufferedLogger(botId, "NetworkLogger", networkAccess, 100), localLogger, ramMemoryLogger);
	}
	
	public LogDistributionManager(int botId, IDataLogger networkLogger, IDataLogger localLogger, IDataLogger ramMemoryLogger)
	{
		initialize(botId, networkLogger, localLogger, ramMemoryLogger);
	}
	
	protected void initialize(int botId, IDataLogger networkLogger, IDataLogger localLogger, IDataLogger ramMemoryLogger)
	{
		this.networkLogger = networkLogger;
		this.localLogger = localLogger;
		this.ramMemoryLogger = ramMemoryLogger;
		watcher = new LoggerWorkloadWatcher();
		
		new Thread(networkLogger).start();
		new Thread(localLogger).start();
		new Thread(ramMemoryLogger).start();
		new Thread(watcher).start();
	}
	
	protected void finalize() throws Throwable
	{
		watcher.running.set(false);
	}
	
	public void log(Data data) {
		
		switch (saveLocation)
		{
		case NETWORK:
			networkLogger.log(data);
			break;
		case LOCAL:
			localLogger.log(data);
			break;
		case RAM:
			ramMemoryLogger.log(data);
			break;
		}
	}
	
}
