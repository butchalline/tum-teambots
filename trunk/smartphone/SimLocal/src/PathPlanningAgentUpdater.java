
import java.util.concurrent.atomic.AtomicBoolean;

import android.graphics.PointF;

import teambot.common.PositionOrientationSupplier;
import teambot.common.data.PositionOrientation;
import teambot.common.interfaces.IDistanceListener;
import teambot.common.interfaces.IPositionListener;
import teambot.communication.TBInfraredData;
import teambot.communication.TBPositionData;
import teambot.pathplanning.Agent;
import teambot.simulator.SimulatorProxy;
import teambot.simulator.SimulatorProxy.PositionInfraredData;

public class PathPlanningAgentUpdater implements Runnable {

	public static final int offsetY = 10000;
	
	protected AtomicBoolean running = new AtomicBoolean(false);
	protected SimulatorProxy simulator;
	protected Agent agent;
	protected PositionOrientation position;
	protected long timeBetweenUpdates;
	private IDistanceListener distanceListener;
	private PositionOrientationSupplier positionSupplier = new PositionOrientationSupplier(new PointF(90.0f, 96.25f));
	private IPositionListener positionListener = positionSupplier;
	public AtomicBoolean wasUpdated = new AtomicBoolean(false);

	public PathPlanningAgentUpdater(SimulatorProxy proxy, Agent agent, PositionOrientation position,
			IPositionListener positionListener, IDistanceListener distanceListener, int updateCycleInHz) {
		this.simulator = proxy;
		this.agent = agent;
		this.position = position;
		timeBetweenUpdates = (long) (1000 / updateCycleInHz);
		this.positionSupplier.register(positionListener);
		this.distanceListener = distanceListener;
	}

	@Override
	public void run() {
		running.set(true);

		long lastCycleStart = 0;

		while (running.get()) {

			while (System.currentTimeMillis() <= lastCycleStart + timeBetweenUpdates) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			lastCycleStart = System.currentTimeMillis();

			PositionInfraredData data = simulator.getLastMeasurement();

			TBInfraredData infraredMeasurement = data.infraredData;
			if (infraredMeasurement == null)
				continue;

			TBPositionData positionMeasurement = data.positionData;
			if (positionMeasurement == null)
				continue;

			float newAngle = PositionOrientation.normalizeAngle_plusMinus180(-positionMeasurement.angle * 0.01f + 90f);

			if(position == null)
				position = new PositionOrientation(positionMeasurement.x, offsetY - positionMeasurement.y, newAngle);
			
			position.setX(positionMeasurement.x);
			position.setY(10000 - positionMeasurement.y);
			position.setAngleInDegree(newAngle);
			
			positionListener.callback_PositionChanged(position);
			distanceListener.callback_NewMeasurement(infraredMeasurement.middleDistance * 10);
			
			if(wasUpdated.get() == false)
				wasUpdated.set(true);
			
			agent.addMeasurement(position.getPosition(), position.getAngleInRadian(), infraredMeasurement.leftDistance & 0xFF,
					infraredMeasurement.middleDistance & 0xFF, infraredMeasurement.rightDistance & 0xFF);
		}
	}

}
