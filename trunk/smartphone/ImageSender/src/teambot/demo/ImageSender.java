package teambot.demo;

import teambot.common.Camera;
import teambot.common.NetworkConnection;
import teambot.common.ServerLogger;
import teambot.common.data.ByteArrayData;
import teambot.common.data.DataType;
import teambot.common.interfaces.IImageProcessor;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class ImageSender extends Activity {

	NetworkConnection networkConnection = new NetworkConnection();
	ServerLogger logger = new ServerLogger("192.168.178.10", "8000", "Logger");
	
	Camera camera;
	ToggleButton streamToggle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_sender);
		
		streamToggle = (ToggleButton)findViewById(R.id.toggleButton_StreamImages);		
		((Button)findViewById(R.id.button_SendImage)).setOnClickListener(listener_SendImage);
		
		camera = new Camera();
		camera.registerCallback(new StreamCallback());
	}
	
    private OnClickListener listener_SendImage = new OnClickListener() {
		@Override
        public synchronized void onClick(View v) {
			logger.save(new ByteArrayData(camera.getLatestImage(), DataType.PICTURE));
		}
    };
    
    private class StreamCallback implements IImageProcessor {

		@Override
		public void processImage(byte[] image) {
			if(streamToggle.isChecked())
				logger.save(new ByteArrayData(image, DataType.PICTURE));			
		}
    };
}
