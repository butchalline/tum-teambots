#ifndef DATAINTERFACE_ICE
#define DATAINTERFACE_ICE


module Communication
{
	enum DataTypeIce {
		PICTURE,
		DEBUG,
		INFO,
		LOGGERINFO,
		ACCELEROMETER,
		GYROSCOPE,
		UNSPECIFIED
	};

	sequence<byte> byteSequence;
	class ByteData
	{
		DataTypeIce type;
		long timeStamp;
		byteSequence byteArrayData;
			
	};

	class Image
	{
		long timeStamp;	
		int width;
		int height;
		byteSequence data;			
	};
		
	interface ImageInterface
	{
	    idempotent void send(Image anImage);
	};

	sequence<float> floatSequence;
	class FloatData
	{
		DataTypeIce type;
		long timeStamp;	
		floatSequence floatArrayData;
			
	};

	interface DataInterface
	{
	    idempotent void sendByteData(ByteData data);
	    idempotent void sendFloatData(FloatData data);
	};

};

#endif
