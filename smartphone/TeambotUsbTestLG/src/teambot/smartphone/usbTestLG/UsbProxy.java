package teambot.smartphone.usbTestLG;

import java.io.IOException;

public interface UsbProxy {
	
	public int read(byte[] b) throws IOException;
	public void write(byte[] b) throws IOException;
}
