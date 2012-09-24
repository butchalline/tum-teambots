package dataLogger;

import java.util.Map;

import teambotData.Data;
import teambotData.DataType;

public class IceDataMapper {

	static protected Map<DataType, Communication.DataTypeIce> dataToIceDataMap;

	static public Communication.Data map(Data data)
	{
		Communication.DataTypeIce iceType = Communication.DataTypeIce.UNSPECIFIED;
		 
		switch (data.getType())
		{
		case PICTURE:
			iceType = Communication.DataTypeIce.PICTURE;
			break;
		case DEBUG:
			iceType = Communication.DataTypeIce.DEBUG;
			break;
		case INFO:
			iceType = Communication.DataTypeIce.INFO;
			break;
		case LOGGER_INFO:
			iceType = Communication.DataTypeIce.LOGGERINFO;
			break;
		case ACCELEROMETER:
			iceType = Communication.DataTypeIce.ACCELEROMETER;
			break;
		case GYROSCOPE:
			iceType = Communication.DataTypeIce.GYROSCOPE;
			break;
		}
		
		return new Communication.Data(iceType, data.getTimestamp(), data.dataAsByteArray());
	}
}
