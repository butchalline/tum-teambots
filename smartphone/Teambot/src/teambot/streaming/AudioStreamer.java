package teambot.streaming;

import org.apache.commons.lang3.ArrayUtils;

import teambot.common.Bot;
import teambot.common.Settings;
import teambot.common.utils.SimpleEndlessThread;
import teambot.remote.IStreamReceiverPrx;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

public class AudioStreamer extends SimpleEndlessThread
{
	AudioRecord _recorder = null; 
	protected byte[] _buffer;
	
	public AudioStreamer()
	{
		int frequency = 44100;
		int bufferSize = AudioRecord.getMinBufferSize(frequency, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * Settings.soundBufferSizeMultiplier;
		_buffer = new byte[bufferSize];
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		_recorder = new AudioRecord(AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize);
		_recorder.startRecording();
		System.out.println("AudioStreamer ready, buffer size: " + bufferSize);
	}

	@Override
	protected void doInThreadLoop()
	{
		int numberOfReadBytes = _recorder.read(_buffer, 0, _buffer.length);
		
		byte[] bufferSlice = ArrayUtils.subarray(_buffer, 0, numberOfReadBytes);
		
		for(IStreamReceiverPrx streamReceiver : Bot.getStreamReceivers())
		{
			streamReceiver.begin_audioCallback(bufferSlice);
//				System.out.println("Audio packet dispatched to " +streamReceiver.ice_getIdentity().name);
//				System.out.println(streamReceiver.ice_getConnection()._toString());
				
		}
	}

}