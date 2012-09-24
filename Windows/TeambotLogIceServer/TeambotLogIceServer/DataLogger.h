#pragma once

#include <vector>

#include "DataInterface.h"

class DataLogger : public Communication::DataInterface
{
public:
	virtual void sendData(const Communication::DataPtr& data, const Ice::Current&);
};