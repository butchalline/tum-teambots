package teambot.common.usb;

import teambot.common.utils.ByteHelper;
import teambot.common.utils.IllegalByteValueException;

public enum UsbHeader {
	
	//TB_COMMAND_ID		
	TB_COMMAND_REQUESTSTATE_VELOCITYDRIVE	(0x00, 0x00),
	TB_COMMAND_REQUESTSTATE_POSITIONDRIVE	(0x00, 0x01),
	TB_COMMAND_REQUESTSTATE_TURN			(0x00, 0x02),
	TB_COMMAND_REQUESTSTATE_STOP			(0x00, 0x03),
	TB_COMMAND_RESET						(0x00, 0x04),
	
	//TB_VELOCITY_ID
	TB_VELOCITY_FORWARD		(0x01, 0x00),
	TB_VELOCITY_BACKWARD	(0x01, 0x01),
	/**
	 * Right wheel goes forwards, left goes backwards
	 */
	TB_VELOCITY_TURN_LEFT	(0x01, 0x02),
	/**
	 * Left wheel goes forwards, right goes backwards
	 */
	TB_VELOCITY_TURN_RIGHT	(0x01, 0x03),
		
	//TB_POSITION_ID
	/**
	 * In millimeters
	 */
	TB_POSITION_FORWARD		(0x02, 0x00),
	/**
	 * In millimeters
	 */
	TB_POSITION_BACKWARD	(0x02, 0x01),
	/**
	 * Distance as degrees * 100
	 */
	TB_POSITION_TURN_RIGHT	(0x02, 0x02),
	/**
	 * Distance as degrees * 100
	 */
	TB_POSITION_TURN_LEFT	(0x02, 0x03),

	
	//TB_DATA_ID
	/**
	 * Distance left | distance middle | distance right
	 */
	TB_DATA_INFRARED	(0x03, 0x00),
	
	
	//TB_ERROR_ID
	TB_ERROR_TRACE		(0x42, 0x00),
	TB_ERROR_DEBUG		(0x42, 0x01),
	TB_ERROR_LOG		(0x42, 0x02),
	TB_ERROR_INFO		(0x42, 0x03),
	TB_ERROR_ERROR		(0x42, 0x04);
	
	
	protected byte id = (byte) 0xFF;
	protected byte subId = (byte) 0xFF;
	protected byte[] timestamp = new byte[2];
	
	UsbHeader(int id, int subId) {
		this.id = (byte)id;
		this.subId = (byte)subId;
	}
	
	public void setTimestamp(int timestamp) throws IllegalByteValueException {
		this.timestamp[0] = ByteHelper.checkedByteConversion(timestamp & 0xFF00 >> 8);
		this.timestamp[1] = ByteHelper.checkedByteConversion(timestamp & 0x00FF);
	}
	
	public void setTimestamp(long timestamp) throws IllegalByteValueException {
		this.timestamp[0] = ByteHelper.checkedByteConversion(timestamp & 0xFF00 >> 8);
		this.timestamp[1] = ByteHelper.checkedByteConversion(timestamp & 0x00FF);
	}
	
	public byte getId() {
		return id;
	}
	
	public byte getSubId() {
		return subId;
	}
	
	public byte[] asByteArray() {
		byte[] header = { id, subId, timestamp[0], timestamp[1]};
		return header;
	}
}
