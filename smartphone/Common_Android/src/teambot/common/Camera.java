package teambot.common;

import java.util.Vector;

import teambot.common.interfaces.ICamera;
import teambot.common.interfaces.IImageProcessor;
import android.hardware.Camera.PreviewCallback;

public class Camera implements ICamera {

	android.hardware.Camera camera;
	byte[] lastImage = new byte[640*480];
	Vector<IImageProcessor> processors = new Vector<IImageProcessor>();

	public Camera() {
		camera = android.hardware.Camera.open();

		android.hardware.Camera.Parameters params = camera.getParameters();
		params.setPreviewSize(640, 480);
		camera.setParameters(params);

		camera.setPreviewCallback(new CameraCallback());
		camera.startPreview();
	}

	private class CameraCallback implements PreviewCallback {

		@Override
		public synchronized void onPreviewFrame(byte[] data,
				android.hardware.Camera camera) {
			lastImage = data;
			for (IImageProcessor processor : processors)
				processor.processImage(lastImage);
		}

	}

	@Override
	public void registerCallback(IImageProcessor processor) {
		processors.add(processor);
	}

	@Override
	public synchronized byte[] getLatestImage() {
		return lastImage;
	}

}
