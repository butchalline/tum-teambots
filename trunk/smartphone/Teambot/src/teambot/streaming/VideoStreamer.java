package teambot.streaming;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import teambot.common.Bot;
import teambot.common.utils.SimpleEndlessThread;
import teambot.common.utils.ThreadUtil;
import teambot.remote.BitmapSlice;
import teambot.remote.IStreamReceiverPrx;
import android.graphics.Bitmap;
import android.util.Log;

public class VideoStreamer extends SimpleEndlessThread
{
	private static final String TAG = "VideoStreamer";
	protected VideoCapture _CameraCapture;

	public VideoStreamer()
	{
		openCamera();
		setupCamera(640, 480);
	}

	public boolean openCamera()
	{
		Log.i(TAG, "openCamera");
		synchronized (this)
		{
			releaseCamera();
			_CameraCapture = new VideoCapture(Highgui.CV_CAP_ANDROID);
			if (!_CameraCapture.isOpened())
			{
				_CameraCapture.release();
				_CameraCapture = null;
				Log.e(TAG, "Failed to open native camera");
				return false;
			}
		}
		return true;
	}

	public void releaseCamera()
	{
		Log.i(TAG, "releaseCamera");
		synchronized (this)
		{
			if (_CameraCapture != null)
			{
				_CameraCapture.release();
				_CameraCapture = null;
			}
		}
	}

	public void setupCamera(int width, int height)
	{
		Log.i(TAG, "setupCamera(" + width + ", " + height + ")");
		synchronized (this)
		{
			if (_CameraCapture != null && _CameraCapture.isOpened())
			{
				List<Size> sizes = _CameraCapture.getSupportedPreviewSizes();
				int mFrameWidth = width;
				int mFrameHeight = height;

				// selecting optimal camera preview size
				{
					double minDiff = Double.MAX_VALUE;
					for (Size size : sizes)
					{
						if (Math.abs(size.height - height) + Math.abs(size.width - width) < minDiff)
						{
							mFrameWidth = (int) size.width;
							mFrameHeight = (int) size.height;
							minDiff = Math.abs(size.height - height) + Math.abs(size.width - width);
						}
					}
				}

				_CameraCapture.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, mFrameWidth);
				_CameraCapture.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, mFrameHeight);
				Log.i(TAG, "Camera set to: " + mFrameWidth + " x " + mFrameHeight);
			}
		}
	}

	protected void streamFrame(VideoCapture capture)
	{
		Mat mRgba = new Mat();
		if (!capture.retrieve(mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA))
			return;

		Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(mRgba, bmp);
		
		ByteArrayOutputStream bitmapStream_png = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream_png);

		for (IStreamReceiverPrx streamReceiver : Bot.getStreamReceivers())
			streamReceiver.begin_bitmapCallback(new BitmapSlice(bmp.getWidth(), bmp.getHeight(), bitmapStream_png.toByteArray()));
//		System.out.println("Image dispatched");
		bmp.recycle();
		mRgba.release();
	}

	@Override
	protected void doInThreadLoop()
	{
		synchronized (this)
		{
			if (_CameraCapture == null)
				return;

			if (!_CameraCapture.grab())
			{
				Log.e(TAG, "mCamera.grab() failed");
				return;
			}

			streamFrame(_CameraCapture);
			ThreadUtil.sleepMSecs(10);
		}
	}

	public void stop()
	{
		releaseCamera();
		super.stop();
	}
}
