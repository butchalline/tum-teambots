#pragma once

#include <vector>
#include "opencv2/core/core.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"

#include "DataInterface.h"

class DataLogger : public Communication::DataInterface
{
public:
	virtual void sendByteData(const Communication::ByteDataPtr& data, const Ice::Current&);
	virtual void sendFloatData(const Communication::FloatDataPtr& data, const Ice::Current&);

private:
	cv::Mat image;
};