package teambot.teambot;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class TeambotActivity extends Activity
{
	private TeambotAsyncTask _teambotTask;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teambot);

		_teambotTask = new TeambotAsyncTask();
		String ip = IpHelper.getIPAddress();

		if (ip == "")
		{
			System.out.println("Seems like the network interface has no ip -> turn WLAN on");
		}
		
		_teambotTask.execute(ip);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_teambot, menu);
		return true;
	}
}
