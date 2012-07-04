package teambots.smartphone.usbInterface;

import java.util.ArrayList;
import org.apache.commons.lang3.ArrayUtils;

import teambots.smartphone.utilities.Transformations;

import android.util.Log;


public class PackageExtractor {

	static final String TAG = "PackageExtractor";
	
	int packageId;
	int fullPackageLength;
	int numberOfProcessedBytes = 0;
	byte[] header = new byte[UsbPackage.headerSize];
	int headerIndex = 0;
	ArrayList<byte[]> data = new ArrayList<byte[]>(Receiver.MAX_PACKAGE_SIZE); //TODO Check if big waste of ram
	public ArrayList<UsbPackage> finishedPackages = new ArrayList<UsbPackage>(10);
	
	public void add(int numberOfReceivedBytes, byte[] buffer)
	{
		buffer = ArrayUtils.subarray(buffer, 0, numberOfReceivedBytes);
		
		int numberOfRemainingHeaderBytes = header.length - numberOfProcessedBytes;
		
		if(numberOfRemainingHeaderBytes > 0)
		{
			if(fillUpHeader(buffer))
			{
				buffer = ArrayUtils.subarray(buffer, numberOfRemainingHeaderBytes, buffer.length);
				packageId = Transformations.signedByteToUnsignedInt(header[0]) << 8;
				packageId += header[1];
				fullPackageLength = Message.IntIdToType.get(packageId).packageLength + header.length;
				numberOfProcessedBytes += numberOfRemainingHeaderBytes;
				Log.v(TAG, "Received Package - id: " + packageId + "; length: " + fullPackageLength);
			}
			else
			{
				numberOfProcessedBytes += buffer.length;
				return;
			}
		}
		
		numberOfProcessedBytes += buffer.length;
		Log.v(this.getClass().getName(), "Processed bytes, length: " + numberOfProcessedBytes);
		
		byte[] bytesOfNextPackage = new byte[0];
		
		if(numberOfProcessedBytes > fullPackageLength)
		{
			int numberOfBytesFromNextPackage = numberOfProcessedBytes - fullPackageLength;
			bytesOfNextPackage = ArrayUtils.subarray(buffer, buffer.length - numberOfBytesFromNextPackage, buffer.length-1);
			buffer = ArrayUtils.subarray(buffer, 0, buffer.length - numberOfBytesFromNextPackage);
			numberOfProcessedBytes = fullPackageLength;
		}
		this.data.add(buffer);
		
		if(numberOfProcessedBytes == fullPackageLength)
		{
			finishedPackages.add(new UsbPackage(packageId, concatedCurrentPackageData())); //TODO don't convert to byte
			this.data.clear();
			numberOfProcessedBytes = 0;
			Log.v(TAG, "Extraction of package finished, id = " + packageId);
		}
		
		if(bytesOfNextPackage.length > 0)
			add(bytesOfNextPackage.length, bytesOfNextPackage);
	}
	
	
	private boolean fillUpHeader(byte[] buffer)
	{
		int bufferIndex = 0;
		while(true)
		{
			header[headerIndex] = buffer[bufferIndex];
			headerIndex++;
			
			if(headerIndex == header.length)
			{
				headerIndex = 0;
				return true;
			}
			
			bufferIndex++;
			if(bufferIndex == buffer.length)
				return false;
		}		
	}
	
	private byte[] concatedCurrentPackageData()
	{
		byte[] data = new byte[0];
		
		for(int dataSlizeIndex = 0; dataSlizeIndex < this.data.size(); dataSlizeIndex++)
			data = ArrayUtils.addAll(data, this.data.get(dataSlizeIndex));
		
		return data;
	}
	
}
