
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
	
	sequence<float> floatSequence;
	class FloatData
	{
		DataTypeIce type;
		long timeStamp;	
		floatSequence floatArrayData;
			
	};

	interface ILoggerData
	{
		idempotent void sendByteData(ByteData data);
		idempotent void sendFloatData(FloatData data);
	};
};
