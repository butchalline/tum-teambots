package teambot.streaming;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
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
import android.graphics.Point;
import android.util.Log;

public class VideoStreamer extends SimpleEndlessThread
{
	private static final String TAG = "VideoStreamer";
	protected VideoCapture _CameraCapture;
	protected Point _imageSize = new Point(320, 240);
	protected Mat _capturedImage = new Mat(_imageSize.x, _imageSize.y, CvType.CV_8UC4);

	public VideoStreamer()
	{
		openCamera();
		setupCamera(_imageSize.x, _imageSize.y);
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
		// long timestamp = System.currentTimeMillis();

		if (!capture.retrieve(_capturedImage, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA))
			return;

		Bitmap bmp = Bitmap.createBitmap(_capturedImage.cols(), _capturedImage.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(_capturedImage, bmp);
		//
		// long timestamp2 = System.currentTimeMillis();
		// System.out.println("Frame capture took: " + (timestamp2 -
		// timestamp));

		ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, bitmapStream);

		// timestamp = System.currentTimeMillis();
		// System.out.println("Compressing took: " + (timestamp - timestamp2));

		for (IStreamReceiverPrx streamReceiver : Bot.getStreamReceivers())
		{
			Ice.AsyncResult r = streamReceiver.begin_bitmapCallback(new BitmapSlice(bmp.getWidth(), bmp.getHeight(), bitmapStream
					.toByteArray()));
			streamReceiver.end_bitmapCallback(r);
		}
		System.out.println("Image dispatched");

		bmp.recycle();
		bmp = null;

		try
		{
			bitmapStream.reset();
			bitmapStream.close();			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
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
