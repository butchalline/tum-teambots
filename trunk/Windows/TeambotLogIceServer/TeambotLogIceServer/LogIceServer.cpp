
#include <Ice/Ice.h>
#include "DataInterface.h"
#include "DataLogger.h"
#include <vector>

using namespace std;

int main(int argc, char* argv[])
{
    Ice::CommunicatorPtr communicator;

    //try
    //{
		Ice::PropertiesPtr props = Ice::createProperties(argc, argv);
		props->setProperty("Ice.Trace.Network", "2");
		props->setProperty("Ice.Trace.Protocol", "2");
		Ice::InitializationData id;
		id.properties = props;

        communicator = Ice::initialize(id);

		//Ice::ObjectAdapterPtr adapter = communicator->createObjectAdapterWithEndpoints("ControlServer", "udp -p 9030");
		//adapter->add(new DataLogger, communicator->stringToIdentity("Logger"))->ice_datagram();
		//adapter->activate();

		Communication::DataInterfacePrx botProxy = Communication::DataInterfacePrx::uncheckedCast(
			communicator->stringToProxy("InterfaceBot1:tcp -h 131.159.195.64 -p 9004"));//->ice_datagram());

		cout << "DataLogger server ready." << endl;
		vector<byte> byteData;
		byteData.push_back(1);
		byteData.push_back(45);
		byteData.push_back(23);
		byteData.push_back(25);
		byteData.push_back(120);
		byteData.push_back(42);

		Communication::ByteData* testData = new Communication::ByteData(Communication::DataTypeIce::ACCELEROMETER, 0, byteData);
		botProxy->sendByteData(testData);

        communicator->waitForShutdown();
        communicator->destroy();
   // }
    //catch(const Ice::Exception& ex)
    //{
    //    cerr << ex << endl;
    //    if(communicator)
    //    {
    //        try
    //        {
    //            communicator->destroy();
    //        }
    //        catch(const Ice::Exception& ex)
    //        {
    //            cerr << ex << endl;
    //        }
    //    }
    //    exit(1);
    //}
    return 0;
}