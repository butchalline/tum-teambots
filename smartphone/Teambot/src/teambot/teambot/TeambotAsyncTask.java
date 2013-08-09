package teambot.teambot;

import teambot.DisplayInformation;
import teambot.common.Bot;
import teambot.common.interfaces.IInformationDisplayer;
import teambot.common.interfaces.IUsbIO;
import android.os.AsyncTask;

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
		Bot bot = new Bot(params[0], _usbIo, this);
		bot.run();
		
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
