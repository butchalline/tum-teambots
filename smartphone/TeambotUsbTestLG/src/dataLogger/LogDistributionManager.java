package dataLogger;

import java.util.concurrent.atomic.AtomicBoolean;

import teambotData.Data;

public class LogDistributionManager {
	
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
			LoggerStatus lastNetworkStatus = LoggerStatus.IDLE;
			LoggerStatus lastRamStatus = LoggerStatus.IDLE;
			
			while(running.get())
			{	
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				networkStatus = networkLogger.getStatus();			
//				networkLogger.log(new teambotData.LoggerInfo("!!!! network load: " + networkStatus));
				if (networkStatus != LoggerStatus.CRITICAL && networkStatus != LoggerStatus.AT_LIMIT)
				{														
					saveLocation = SaveLocation.NETWORK;
					lastNetworkStatus = networkStatus;
					continue;
				}
				if (networkStatus == LoggerStatus.CRITICAL && lastNetworkStatus != LoggerStatus.CRITICAL)
				{
					networkLogger.log(new teambotData.LoggerInfo("!!!! networkLogger is critical !!!!!!!!"));
					lastNetworkStatus = LoggerStatus.CRITICAL;
				}
				
				diskStatus = localLogger.getStatus();
				if (diskStatus != LoggerStatus.CRITICAL && diskStatus != LoggerStatus.AT_LIMIT)
				{
					saveLocation = SaveLocation.LOCAL;
					continue;
				}
				
				saveLocation = SaveLocation.RAM;
				
				if (memoryStatus == LoggerStatus.CRITICAL && lastRamStatus != LoggerStatus.CRITICAL)
					networkLogger.log(new teambotData.LoggerInfo("!!!! ramLogger is critical"));
				
				if(memoryStatus == LoggerStatus.AT_LIMIT && lastRamStatus != LoggerStatus.AT_LIMIT)
					networkLogger.log(new teambotData.LoggerInfo("!!!! ramLogger is at limit"));
				
				lastRamStatus = memoryStatus;
				
			}
			
		}
		
	}
	
	
	public LogDistributionManager(NetworkAccess networkAccess)
	{
		initialize(new BufferedLogger("NetworkLogger", networkAccess, 1000),
				new DisabledDataLogger(),
				new DisabledDataLogger());
	}
	
	public LogDistributionManager(NetworkAccess networkAccess, IDataLogger localLogger, IDataLogger ramMemoryLogger)
	{
		initialize(new BufferedLogger("NetworkLogger", networkAccess, 100), localLogger, ramMemoryLogger);
	}
	
	public LogDistributionManager(IDataLogger networkLogger, IDataLogger localLogger, IDataLogger ramMemoryLogger)
	{
		initialize(networkLogger, localLogger, ramMemoryLogger);
	}
	
	protected void initialize(IDataLogger networkLogger, IDataLogger localLogger, IDataLogger ramMemoryLogger)
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
			if(networkLogger.log(data))
				break;
		case LOCAL:
			if(localLogger.log(data))
				break;
		case RAM:
			if(ramMemoryLogger.log(data))
				break;
		}
	}
	
}
