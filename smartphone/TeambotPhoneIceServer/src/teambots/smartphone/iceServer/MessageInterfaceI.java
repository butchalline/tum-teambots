package teambots.smartphone.iceServer;

import android.util.Log;
import Communication.*;
import Ice.Current;

@SuppressWarnings("serial")
public class MessageInterfaceI extends _MessageInterfaceDisp  {

	@Override
	public short[] fetchData(Current __current) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendData(short[] data, Current __current) {
		Log.d("Ice Server", "Data arrived: " + data);
		
		for(int i = 0; i < data.length; i++)
			Log.d("Ice Server", i + ": " + data[i]);
		
	}

}
