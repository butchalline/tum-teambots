package teambot.common.usb;

import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

import teambot.common.utils.ByteHelper;
import teambot.common.utils.IllegalByteValueException;

public enum UsbHeader {
	
	//TB_COMMAND_ID		
	TB_COMMAND_REQUESTSTATE_VELOCITYDRIVE	(0x00, 0x00, 0),
	TB_COMMAND_REQUESTSTATE_POSITIONDRIVE	(0x00, 0x01, 0),
	TB_COMMAND_REQUESTSTATE_TURN			(0x00, 0x02, 0),
	TB_COMMAND_REQUESTSTATE_STOP			(0x00, 0x03, 0),
	TB_COMMAND_RESET						(0x00, 0x04, 0),
	
	//TB_VELOCITY_ID
	TB_VELOCITY_FORWARD		(0x01, 0x00, 2),
	TB_VELOCITY_BACKWARD	(0x01, 0x01, 2),
	/**
	 * Right wheel goes forwards, left goes backwards
	 */
	TB_VELOCITY_TURN_LEFT	(0x01, 0x02, 2),
	/**
	 * Left wheel goes forwards, right goes backwards
	 */
	TB_VELOCITY_TURN_RIGHT	(0x01, 0x03, 2),
		
	//TB_POSITION_ID
	/**
	 * In millimeters
	 */
	TB_POSITION_FORWARD		(0x02, 0x00, 2),
	/**
	 * In millimeters
	 */
	TB_POSITION_BACKWARD	(0x02, 0x01, 2),
	/**
	 * Distance as 1/100 degrees 
	 */
	TB_POSITION_TURN_RIGHT	(0x02, 0x02, 2),
	/**
	 * Distance as 1/100 degrees
	 */
	TB_POSITION_TURN_LEFT	(0x02, 0x03, 2),

	
	//TB_DATA_ID
	/**
	 * The associated data is the distance in cm
	 */
	TB_DATA_INFRARED	(0x03, 0x00, 1),
	
	/**
	 *  2 bytes left change in ??? TODO
	 *  2 bytes right change in ???
	 */
	TB_DATA_WHEEL_CHANGES	(0x03, 0x01, 4),
	
	/**
	 * Mock headers only used for testing without the micro controller 
	 */
	//TB_MOCK
	/**
	 * [xPos_mm][xPos_mm][yPos_mm][yPos_mm][angle_centiDeg][angle_centiDeg]
	 */
	TB_MOCK_POSITION_CHANGE (0x04, 0x00, 6),
	
	//TB_ERROR_ID
	TB_ERROR_TRACE		(0x42, 0x00, 0),
	TB_ERROR_DEBUG		(0x42, 0x01, 0),
	TB_ERROR_LOG		(0x42, 0x02, 0),
	TB_ERROR_INFO		(0x42, 0x03, 0),
	TB_ERROR_ERROR		(0x42, 0x04, 0);
	
	
	public static final int timeStampSize = 2;
	
	protected byte _id = (byte) 0xFF;
	protected byte _subId = (byte) 0xFF;
	protected byte[] _timestamp = new byte[timeStampSize];
	protected int _dataByteCount;
	
	UsbHeader(int id, int subId, int dataByteCount) {
		_id = (byte)id;
		_subId = (byte)subId;
		_dataByteCount = dataByteCount;
	}
	
	@SuppressWarnings("serial")
	private static final HashMap<SimpleEntry<Byte, Byte>, UsbHeader> headerMap = new HashMap<SimpleEntry<Byte, Byte>, UsbHeader>()
	{
		{
			for (UsbHeader headerEnum : UsbHeader.values())
			{
				put(new SimpleEntry<Byte, Byte>(headerEnum._id, headerEnum._subId), headerEnum);
			}
		}
	}; 
	
	public static UsbHeader getHeader(byte id, byte subId)
	{
		return headerMap.get(new SimpleEntry<Byte, Byte>(id, subId));
	}
	
	static public int getHeaderLength()
	{
		return TB_COMMAND_REQUESTSTATE_VELOCITYDRIVE.asByteArray().length;
	}
	
	public void setTimestamp(int timestamp) throws IllegalByteValueException {
		_timestamp[0] = ByteHelper.checkedByteConversion(timestamp & 0xFF00 >> 8);
		_timestamp[1] = ByteHelper.checkedByteConversion(timestamp & 0x00FF);
	}
	
	public void setTimestamp(long timestamp) throws IllegalByteValueException {
		_timestamp[0] = ByteHelper.checkedByteConversion(timestamp & 0xFF00 >> 8);
		_timestamp[1] = ByteHelper.checkedByteConversion(timestamp & 0x00FF);
	}
	
	public void setTimestamp(byte timestamp_part1, byte timestamp_part2) {
		_timestamp[0] = timestamp_part1;
		_timestamp[1] = timestamp_part2;
	}
	
	public byte getId() {
		return _id;
	}
	
	public byte getSubId() {
		return _subId;
	}
	
	public int getTimestamp() {		
		return (_timestamp[0] << 8) | _timestamp[1];
	}
	
	public int getDataByteCount() {
		return _dataByteCount;
	}
	
	public byte[] asByteArray() {
		byte[] header = { _id, _subId, _timestamp[0], _timestamp[1]};
		return header;
	}
}
