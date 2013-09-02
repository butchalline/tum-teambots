package teambot.remote;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

import teambot.common.utils.ThreadUtil;

public class IOInputStream extends InputStream
{
	ConcurrentLinkedQueue<Byte> _buffer = new ConcurrentLinkedQueue<Byte>();

	@Override
	public int read() throws IOException
	{
		Byte newByte = _buffer.poll();
		if (newByte == null)
			return -1;
		return newByte;
	}

	public void insertIntoStream(short[] newValues)
	{
		byte higherByte = 0;
		byte lowerByte = 0;
		for (short newShort : newValues)
		{
			higherByte = (byte) (newShort >> 8);
			lowerByte = (byte) newShort;
			
				while(!_buffer.offer(higherByte))
				{
					ThreadUtil.sleepMSecs(1);
				}
				
				while(!_buffer.offer(lowerByte))
				{
					ThreadUtil.sleepMSecs(1);
				}
		}
	}
}
