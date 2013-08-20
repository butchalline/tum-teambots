package teambot.common.interfaces;

import java.io.IOException;

public interface IUsbIO {
	
	public int read(byte[] buffer) throws IOException;
	public void write(byte[] buffer) throws IOException;
}
