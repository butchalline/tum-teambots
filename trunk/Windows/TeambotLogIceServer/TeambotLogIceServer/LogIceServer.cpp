
#include <Ice/Ice.h>
#include "DataLogger.h"

using namespace std;

int main(int argc, char* argv[])
{
    Ice::CommunicatorPtr communicator;

    try
    {
        communicator = Ice::initialize(argc, argv);
        Ice::ObjectAdapterPtr adapter = communicator->createObjectAdapterWithEndpoints("LogServer", "udp -p 10000");
        adapter->add(new DataLogger, communicator->stringToIdentity("DataLogger"));
        adapter->activate();

		cout << "DataLogger server ready." << endl;
        communicator->waitForShutdown();
        communicator->destroy();
    }
    catch(const Ice::Exception& ex)
    {
        cerr << ex << endl;
        if(communicator)
        {
            try
            {
                communicator->destroy();
            }
            catch(const Ice::Exception& ex)
            {
                cerr << ex << endl;
            }
        }
        exit(1);
    }
    return 0;
}