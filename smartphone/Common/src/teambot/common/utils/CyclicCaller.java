package teambot.common.utils;

import java.util.concurrent.atomic.AtomicBoolean;

import teambot.common.interfaces.ICyclicCallback;

public class CyclicCaller implements Runnable
{
	long _callbackInterval_ms = 30;
	ICyclicCallback _callback;
	public AtomicBoolean running = new AtomicBoolean(true);
	long _timeStamp_ms = System.currentTimeMillis();

	public CyclicCaller(ICyclicCallback callback)
	{
		_callback = callback;
	}
	
	public CyclicCaller(ICyclicCallback callback, long callbackInterval_ms)
	{
		_callback = callback;
		_callbackInterval_ms = callbackInterval_ms;
	}

	@Override
	public void run()
	{
		long sleepTime_ms = 0; 
		while(running.get())
		{				
			sleepTime_ms = _callbackInterval_ms - (System.currentTimeMillis() - _timeStamp_ms);
			_timeStamp_ms = System.currentTimeMillis();
			
			if(sleepTime_ms > 0)
			{
				try {
					Thread.sleep(sleepTime_ms);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			_callback.callback_cyclic((int) _callbackInterval_ms);
		}
	}
}
