#ifndef DATAINTERFACE_ICE
#define DATAINTERFACE_ICE


module Communication
{
	sequence<byte> dataSequence;
	class Data
	{
		dataSequence byteArrayData;
		long timeStamp;		
	};

	interface DataInterface
	{
	    idempotent void sendData(Data dataObject);
	};

};

#endif
