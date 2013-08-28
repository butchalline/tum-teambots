package teambot.common.utils;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SimpleEndlessThread implements Runnable
{
	protected AtomicBoolean running = new AtomicBoolean(false);
	protected Thread _thread;

	@Override
	public void run()
	{
		running.set(true);
		while (running.get())
		{
			doInThreadLoop();
		}
	}

	public synchronized void start()
	{
		if (running.get())
			return;

		_thread = new Thread(this);
		_thread.start();
	}

	public void stop()
	{
		running.set(false);
		synchronized (_thread)
		{
			_thread.notify();
		}
	}

	abstract protected void doInThreadLoop();
}
