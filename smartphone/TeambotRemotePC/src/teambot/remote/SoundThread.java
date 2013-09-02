package teambot.remote;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.SourceDataLine;

import org.apache.commons.lang3.ArrayUtils;

import teambot.common.utils.SimpleEndlessThread;

public class SoundThread extends SimpleEndlessThread
{
	protected int _buffersize = 4096 * 2;
	protected int _numberOfReadBytes = 0;

	protected InputStream _stream;
	protected SourceDataLine _line;
	protected byte[] _buffer = new byte[_buffersize];
	protected byte[] _tempBuffer = new byte[_buffersize];

	SoundThread(InputStream stream, SourceDataLine line)
	{
		this._stream = stream;
		this._line = line;
	}

	@Override
	protected void doInThreadLoop()
	{
		_numberOfReadBytes = 0;
		try
		{
			_buffersize = _stream.read(_tempBuffer);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		if (_buffersize > 0)
		{
			_line.write(_buffer, 0, _buffersize);
//			System.out.println("Wrote " + _numberOfReadBytes + " bytes on line");
		}
	}

}
