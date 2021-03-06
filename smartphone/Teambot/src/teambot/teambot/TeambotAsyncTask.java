package teambot.teambot;

import teambot.DisplayInformation;
import teambot.common.Bot;
import teambot.common.interfaces.IInformationDisplayer;
import teambot.common.interfaces.IUsbIO;
import android.os.AsyncTask;
import teambot.streaming.AudioStreamer;
import teambot.streaming.VideoStreamer;

public class TeambotAsyncTask extends AsyncTask<String, teambot.DisplayInformation, Void> implements IInformationDisplayer
{	
	protected IUsbIO _usbIo;
	
	public TeambotAsyncTask(IUsbIO usbIo)
	{
		_usbIo = usbIo;
	}
	
	@Override
	protected Void doInBackground(String... params)
	{
		Bot bot = new Bot(params[0], this);
		bot.setupUsb(_usbIo);
		AudioStreamer audioStream = new AudioStreamer();
		audioStream.start();
		VideoStreamer videoStreamer = new VideoStreamer();
		videoStreamer.start();
		
		bot.run();
		
		audioStream.stop();
		videoStreamer.stop();
		return null;
	}
	
	@Override
    protected void onProgressUpdate(DisplayInformation... values)
	{
		
	}
	
	@Override
	protected void onPostExecute(Void test)
	{
		System.out.println("finished");
	}

	@Override
	public void display(DisplayInformation info)
	{
		publishProgress(info);
	}

}
