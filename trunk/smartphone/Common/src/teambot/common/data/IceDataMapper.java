package teambot.common.data;

import java.util.Map;

public class IceDataMapper {

	static protected Map<DataType, Communication.DataTypeIce> dataToIceDataMap;

	static public Communication.ByteData map(ByteArrayData data)
	{	
		return new Communication.ByteData(mapType(data.getType()), data.getTimestamp(), data.getData());
	}
	
	static public Communication.FloatData map(FloatArrayData data)
	{	
		return new Communication.FloatData(mapType(data.getType()), data.getTimestamp(), data.getData());
	}
	
	static protected Communication.DataTypeIce mapType(DataType type)
	{
		switch (type)
		{
		case PICTURE:
			return Communication.DataTypeIce.PICTURE;
		case DEBUG:
			return Communication.DataTypeIce.DEBUG;
		case INFO:
			return Communication.DataTypeIce.INFO;
		case LOGGER_INFO:
			return Communication.DataTypeIce.LOGGERINFO;
		case ACCELEROMETER:
			return Communication.DataTypeIce.ACCELEROMETER;
		case GYROSCOPE:
			return Communication.DataTypeIce.GYROSCOPE;
		}
		
		return Communication.DataTypeIce.UNSPECIFIED;
	}
}
