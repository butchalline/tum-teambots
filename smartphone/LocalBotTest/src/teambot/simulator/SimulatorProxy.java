package teambot.simulator;

import teambot.communication.AMD_IDataClient_update;
import teambot.communication.DebugGridPoint;
import teambot.communication.IDataServerPrx;
import teambot.communication.TBFrame;
import teambot.communication.TBInfraredData;
import teambot.communication.TBPositionData;
import teambot.communication.TBPositionReached;
import teambot.communication._IDataClientDisp;
import Ice.Current;

public class SimulatorProxy extends _IDataClientDisp {

	private static final long serialVersionUID = -1726784146178972520L;

	protected TBInfraredData infraredData;
	protected TBPositionData positionData;
	protected TBInfraredData lastInfraredData;
	protected TBPositionData lastPositionData;
	protected boolean isPositionReached = false;
	protected short lastTimeStamp = 0;
	protected boolean lastPackageWasPosition = false;
	
	protected IDataServerPrx _simulatorProxy;
	
	
	public class PositionInfraredData {
		public TBPositionData positionData;
		public TBInfraredData infraredData;
	}
	
	public SimulatorProxy(IDataServerPrx simulatorProxy)
	{
		_simulatorProxy = simulatorProxy;
	}
	
	@Override
	public void update_async(AMD_IDataClient_update __cb, TBFrame data, Current __current) {

		if (data instanceof TBPositionData) {
			lastPositionData = (TBPositionData) data;
		} else if (data instanceof TBPositionReached) {
			TBPositionReached reachedFrame = (TBPositionReached) data;
			setPositionReached();
			lastPositionData = (new TBPositionData(reachedFrame.Id, reachedFrame.SubId, reachedFrame.TimeStamp, reachedFrame.x,
					reachedFrame.y, reachedFrame.angle));
		} else if (data instanceof TBInfraredData) {
			lastInfraredData = (TBInfraredData) data;
		}

		if ((data instanceof TBPositionData || data instanceof TBPositionReached) && data.TimeStamp == lastTimeStamp
				&& !lastPackageWasPosition) {
			updateValues();
		} else if (data instanceof TBInfraredData && data.TimeStamp == lastTimeStamp && lastPackageWasPosition)
			updateValues();

		if (data instanceof TBInfraredData)
			lastPackageWasPosition = false;
		else if (data instanceof TBPositionData || data instanceof TBPositionReached)
			lastPackageWasPosition = true;

		lastTimeStamp = data.TimeStamp;
		__cb.ice_response();
	}

	private synchronized void updateValues() {
		infraredData = lastInfraredData;
		positionData = lastPositionData;
	}

	public synchronized PositionInfraredData getLastMeasurement() {
		PositionInfraredData data = new PositionInfraredData();
		data.infraredData = infraredData;
		data.positionData = positionData;
		return data;
	}

	public synchronized void setPositionReached() {
		isPositionReached = true;
	}

	public synchronized boolean positionIsReached() {
		if (!isPositionReached)
			return false;

		isPositionReached = false;
		return true;
	}

	public void send(TBFrame frame) {
		_simulatorProxy.update(frame);
	}
	
	public void sendDebugMap(DebugGridPoint[] points, short cellSize)
	{
			_simulatorProxy.debugMap(points, cellSize);
	}
}
