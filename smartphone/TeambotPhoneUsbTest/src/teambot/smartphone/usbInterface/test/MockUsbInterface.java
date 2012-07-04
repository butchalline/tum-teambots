package teambot.smartphone.usbInterface.test;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;

import teambots.smartphone.usbInterface.UsbProxy;

public class MockUsbInterface implements UsbProxy {

	byte[] buffer = new byte[0];
	
	@Override
	public int read(byte[] b) throws IOException {
		
		int bufferSize = buffer.length;
		for(int i = 0; i < bufferSize; i++)
			b[i] = buffer[i];
		
		buffer = new byte[0];
		return bufferSize;
	}

	@Override
	public void write(byte[] b) throws IOException {
		buffer = ArrayUtils.addAll(buffer, b);
	}

}
