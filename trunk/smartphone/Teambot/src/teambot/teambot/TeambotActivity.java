package teambot.teambot;

import java.util.concurrent.ExecutionException;

import teambot.usb.UsbConnectionManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class TeambotActivity extends Activity
{
	private TeambotAsyncTask _teambotTask;
	private UsbConnectionManager _usbConnectionManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teambot);

		_usbConnectionManager = new UsbConnectionManager(this);
		
		_usbConnectionManager.start();
		
		_teambotTask = new TeambotAsyncTask(_usbConnectionManager);
		String ip = IpHelper.getIPAddress();

		if (ip == "")
		{
			System.out.println("Seems like the network interface has no ip -> turn WLAN on");
			return;
		}

		_teambotTask.execute(ip);
		
		try
		{
			_teambotTask.get();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		_usbConnectionManager.stop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_teambot, menu);
		return true;
	}
}
