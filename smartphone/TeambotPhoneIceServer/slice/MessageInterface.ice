#ifndef MESSAGEINTERFACE_ICE
#define MESSAGEINTERFACE_ICE

module Communication
{

sequence<short> dataSequence;

	interface MessageInterface
	{
	    dataSequence fetchData();
	    void sendData(dataSequence a);
	};

};

#endif
