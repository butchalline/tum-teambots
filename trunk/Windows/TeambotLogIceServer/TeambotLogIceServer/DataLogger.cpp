#include <Ice/Ice.h>
#include "DataLogger.h"

using namespace std;

void DataLogger::sendData(const Communication::DataPtr& data, const ::Ice::Current&)
{
    cout << "Data logged: " << endl;
	for (unsigned int i = 0; i < data->byteArrayData.size(); i++)
		cout << data->byteArrayData[i];
	cout << endl;
}