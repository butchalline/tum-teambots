import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import teambot.common.Bot;
import teambot.common.Settings;
import teambot.common.data.Pose;
import teambot.common.hardware.BotSensors;
import teambot.common.interfaces.IUsbIO;
import teambot.common.slam.MapConverter;
import teambot.common.slam.Particle;
import teambot.common.slam.ParticleFilter;
import teambot.common.usb.UsbData;
import teambot.common.usb.UsbHeader;
import teambot.common.usb.UsbPacket;
import teambot.common.utils.Constants;
import teambot.communication.TBInfraredData;
import teambot.communication.TBPositionData;
import teambot.pathplanning.Agent;
import teambot.simulator.SimulatorProxy;
import teambot.simulator.SimulatorProxy.PositionInfraredData;

public class PathPlanningAgentUpdater implements Runnable, IUsbIO
{
	protected AtomicBoolean _running = new AtomicBoolean(false);
	protected SimulatorProxy _simulator;
	protected Agent _agent;
	protected Pose _pose;
	protected long _timeBetweenUpdates;
	private ParticleFilter _filter;
	private MapConverter _converter;
	public AtomicBoolean _wasUpdated = new AtomicBoolean(false);

	protected Queue<Byte> _usbStream = new ConcurrentLinkedQueue<Byte>();

	public PathPlanningAgentUpdater(SimulatorProxy proxy, Agent agent, Pose pose, ParticleFilter filter,
			MapConverter converter, int updateCycleInHz)
	{
		_simulator = proxy;
		_agent = agent;
		_pose = pose;
		_timeBetweenUpdates = (long) (1000 / updateCycleInHz);
		Bot.getSensor(BotSensors.DISTANCE).getPoseSupplier().registerForChangeUpdates(filter);
		_filter = filter;
		_converter = converter;
	}

	@Override
	public void run()
	{
		_running.set(true);

		long lastCycleStart = 0;

		while (_running.get())
		{

			while (System.currentTimeMillis() <= lastCycleStart + _timeBetweenUpdates)
			{
				try
				{
					Thread.sleep(1);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			lastCycleStart = System.currentTimeMillis();

			PositionInfraredData data = _simulator.getLastMeasurement();

			TBInfraredData infraredMeasurement = data.infraredData;
			if (infraredMeasurement == null)
				continue;

			TBPositionData positionMeasurement = data.positionData;
			if (positionMeasurement == null)
				continue;

			float newAngle = Pose.normalizeAngle_plusMinus180(-positionMeasurement.angle * 0.01f + 90f);

			float posChangeX_global = positionMeasurement.x - _pose.getX();
			float posChangeY_global = Settings.mapOffsetY - positionMeasurement.y - _pose.getY();

			Pose botPose = Bot.getPose();

			Pose poseChange = new Pose(posChangeX_global, posChangeY_global, (newAngle - _pose.getAngleInDegree())
					* Constants.DegreeToRadian);

			// System.out.println("newAngle: " + newAngle + "; old angle: " +
			// _pose.getAngleInDegree() +"; change angle: " +
			// poseChange.getAngleInDegree());

			poseChange = poseChange.transformPosePosition(-botPose.getAngleInRadian());

			// if(poseChange.getX() != 0)
			// System.out.println("x change agent:" + poseChange.getX());
			// if(poseChange.getY() != 0)
			// System.out.println("y change agent:" + poseChange.getY());

			_pose.setX(positionMeasurement.x);
			_pose.setY(Settings.mapOffsetY - positionMeasurement.y);
			_pose.setAngleInDegree(newAngle);

			byte[] byteDataInfrared =
			{ infraredMeasurement.middleDistance };
			UsbPacket infraredPacket = new UsbPacket(UsbHeader.TB_DATA_INFRARED, new UsbData(byteDataInfrared));

			synchronized (_usbStream)
			{
				for (byte packetByte : infraredPacket.asByteArray())
					_usbStream.add(packetByte);
			}

			Bot.getPoseSupplier().poseChangeCallback(poseChange, true);

			Particle bestParticle = new Particle(_filter.getBestParticle());
			// simulator.sendDebugMap(converter.convertSlamMap(bestParticle.getMap(),
			// bestParticle.getPose()), (short) converter.getCellSize());
			// simulator.sendDebugMap(converter.particlesToMap(filter.getParticles()),
			// (short) converter.getCellSize());
			_simulator.sendDebugMap(
					_converter.convertSlamMapAndParticlePose(bestParticle.getMap(), bestParticle.getPose(),
							_filter.getParticlesCopy()), (short) _converter.getCellSize());
			// simulator.sendDebugMap(converter.convertAverageMap(filter.getParticles(),
			// filter), (short) converter.getCellSize());

			if (_wasUpdated.get() == false)
				_wasUpdated.set(true);

			_agent.addMeasurement(_pose.getPosition(), _pose.getAngleInRadian(),
					infraredMeasurement.leftDistance & 0xFF, infraredMeasurement.middleDistance & 0xFF,
					infraredMeasurement.rightDistance & 0xFF);
		}
	}

	@Override
	public int read(byte[] buffer) throws IOException
	{
		int byteCount;
		synchronized (_usbStream)
		{
			for (byteCount = 0; byteCount < buffer.length && !_usbStream.isEmpty(); ++byteCount)
				buffer[byteCount] = _usbStream.poll();
		}

		return byteCount;
	}

	@Override
	public synchronized void write(byte[] buffer) throws IOException
	{
		System.out.print("Packet (length: " + buffer.length + ") sent to mc: ");

		int i = 1;
		for (byte packetByte : buffer)
		{
			System.out.print("[" + packetByte + "]");
			if (i == UsbHeader.getHeaderLength())
				System.out.print("||");
			i++;
		}

		System.out.println();
	}

}
