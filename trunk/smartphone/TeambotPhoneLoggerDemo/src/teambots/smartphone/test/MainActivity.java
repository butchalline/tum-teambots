package teambots.smartphone.test;

import teambotData.Info;
import dataLogger.LogDistributionManager;
import dataLogger.NetworkAccess;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	LogDistributionManager ldm;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button buttonStart = (Button)findViewById(R.id.button1);       
        buttonStart.setOnClickListener(startListener); 
        
		ldm = new LogDistributionManager(1, new NetworkAccess("192.168.0.107", "10000"));
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    //Create an anonymous implementation of OnClickListener
    private OnClickListener startListener = new OnClickListener() {
		@Override
        public void onClick(View v) {
			ldm.log(new Info(1, "It works."));
           } 
    };
}
