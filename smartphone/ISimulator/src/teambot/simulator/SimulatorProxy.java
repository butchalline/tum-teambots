package teambot.simulator;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import teambot.common.NetworkConnection;
import teambot.common.data.ByteArrayData;
import teambot.common.data.DataType;
import teambot.common.data.IceDataMapper;
import teambot.common.interfaces.IUsbIO;
import Communication.ByteData;
import Communication.DataInterfacePrx;
import Communication.FloatData;
import Communication._DataInterfaceDisp;
import Ice.Current;

public class SimulatorProxy extends _DataInterfaceDisp implements IUsbIO {

	NetworkConnection networkConnection = new NetworkConnection();
	DataInterfacePrx simulator;
	ConcurrentLinkedQueue<byte[]> bufferInput;
	
	public SimulatorProxy(String ip, String port) {
		networkConnection.addLocalUdpProxy(this, "USB", "7000");
		simulator = networkConnection.connectToRemoteTcpProxy("SimulatorUSB", ip, port);
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		b = bufferInput.poll();
		
		if(b == null)
			return 0;
		
		return b.length;
	}

	@Override
	public void write(byte[] b) throws IOException {
		simulator.sendByteData(IceDataMapper.map(new ByteArrayData(b, DataType.SIMULATOR)));
	}

	@Override
	public void sendByteData(ByteData data, Current __current) {
		bufferInput.add(data.byteArrayData);
	}

	@Override
	public void sendFloatData(FloatData data, Current __current) {

	}

}
