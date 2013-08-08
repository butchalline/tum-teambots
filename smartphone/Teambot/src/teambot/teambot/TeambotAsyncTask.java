package teambot.teambot;

import teambot.DisplayInformation;
import teambot.common.Bot;
import teambot.common.interfaces.IInformationDisplayer;
import android.os.AsyncTask;

public class TeambotAsyncTask extends AsyncTask<String, teambot.DisplayInformation, Void> implements IInformationDisplayer
{	
	public TeambotAsyncTask()
	{
		
	}
	
	@Override
	protected Void doInBackground(String... params)
	{
		Bot bot = new Bot(params[0], null, this);
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
