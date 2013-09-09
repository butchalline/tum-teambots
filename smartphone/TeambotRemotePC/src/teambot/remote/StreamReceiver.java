package teambot.remote;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.SourceDataLine;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import Ice.Current;

public class StreamReceiver extends _IStreamReceiverDisp
{
	private static final long serialVersionUID = -9055500091444481319L;
//	private static final String imagePath1 = "src/images/1.jpg";
	
	protected JLabel _labelOfWindowForBitmap;
	protected SourceDataLine _line;

	public StreamReceiver(JLabel labelOfWindow, SourceDataLine line)
	{
		_labelOfWindowForBitmap = labelOfWindow;
		_line = line;
	}

	@Override
	public void bitmapCallback(BitmapSlice newBitmap, Current __current)
	{
		BufferedImage image;
//		System.out.println("New image: " + newBitmap.width + "x" + newBitmap.height);
		try
		{
			image = ImageIO.read(new ByteArrayInputStream(newBitmap.data));
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		
		ImageIcon icon = new ImageIcon(image);
		_labelOfWindowForBitmap.setIcon(icon);
		image.flush();
	}

	@Override
	public void audioCallback(byte[] newAudioBytes, Current __current)
	{
		_line.write(newAudioBytes, 0, newAudioBytes.length);
	}

}
