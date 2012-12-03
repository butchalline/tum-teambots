package teambot.common.interfaces;

import java.io.IOException;

public interface IUsbIO {
	
	public int read(byte[] b) throws IOException;
	public void write(byte[] b) throws IOException;
}
