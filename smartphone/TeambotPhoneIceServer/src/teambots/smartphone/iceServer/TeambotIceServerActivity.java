package teambots.smartphone.iceServer;

import android.app.Activity;
import android.os.Bundle;

public class TeambotIceServerActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Ice.InitializationData initData;
        initData = CommunicationServer.createInitialisationData();
        CommunicationServer app = new CommunicationServer();
        String args[] = new String[1];
        args[0] = "";
        
        app.main("CommunicationServer", args, initData);
    }
}