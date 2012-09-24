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

	sequence<byte> dataSequence;
	class Data
	{
		DataTypeIce type;
		long timeStamp;	
		dataSequence byteArrayData;
			
	};

	interface DataInterface
	{
	    idempotent void sendData(Data dataObject);
	};

};

#endif
