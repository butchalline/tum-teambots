package teambot.common;

import java.util.concurrent.atomic.AtomicBoolean;

import teambot.common.interfaces.ICyclicCallback;

public class CyclicCaller implements Runnable
{
	float _interval_Hz = 60;
	ICyclicCallback _callback;
	public AtomicBoolean running = new AtomicBoolean(true);
	long _timeStamp = System.currentTimeMillis();

	public CyclicCaller(ICyclicCallback callback)
	{
		_callback = callback;
	}
	
	public CyclicCaller(ICyclicCallback callback, float interval_Hz)
	{
		_callback = callback;
		_interval_Hz = interval_Hz;
	}

	@Override
	public void run()
	{
//		long 
		while(running.get())
		{
			
//			if()
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			_callback.callback();
		}
	}
	
	
}
