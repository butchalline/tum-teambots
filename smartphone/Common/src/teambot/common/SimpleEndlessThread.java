package teambot.common;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SimpleEndlessThread implements Runnable
{
	protected AtomicBoolean running = new AtomicBoolean(true);
	
	@Override
	public void run()
	{
		while(running.get())
		{
			doInThreadLoop();
		}
	}
	
	public synchronized void start()
	{
		if(running.get())
			return;
		
		new Thread(this).start();
	}
	
	public void stop(){
		running.set(false);
	}
	
	abstract protected void doInThreadLoop();
}
