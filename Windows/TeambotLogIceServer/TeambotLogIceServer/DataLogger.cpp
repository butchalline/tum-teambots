#include <Ice/Ice.h>
#include <time.h>
#include <math.h>

#include "DataLogger.h"

using namespace std;
using namespace cv;

void DataLogger::sendByteData(const Communication::ByteDataPtr& data, const ::Ice::Current&)
{
	
	if(data->type == Communication::PICTURE) {

		int width = sqrt(1.333334 * data->byteArrayData.size());
		int height = width * 0.75;
		cout << "Image size: " << width << "x" << height << endl;
		Size size(width, height);
		Mat imageWithData = Mat(size, CV_8SC3, &(data->byteArrayData[0])).clone();
		imshow("Image", imageWithData);
		return;
	}


	float ticksTillStart = (float)clock();
	cout << data->timeStamp << ",";
	for (unsigned int i = 0; i < data->byteArrayData.size(); i++)
		cout << data->byteArrayData[i];
	cout << endl;
}

void DataLogger::sendFloatData(const Communication::FloatDataPtr& data, const ::Ice::Current&)
{
	float ticksTillStart = (float)clock();
	cout << data->timeStamp << "; ";
	for (unsigned int i = 0; i < data->floatArrayData.size(); i++)
		cout << data->floatArrayData[i] << "; ";
	cout << endl;
}