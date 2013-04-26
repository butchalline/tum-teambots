
import java.util.concurrent.atomic.AtomicBoolean;

import teambot.common.PositionSupplier;
import teambot.common.data.Position;
import teambot.common.interfaces.IPositionListener;
import teambot.common.utils.Constants;
import teambot.communication.TBInfraredData;
import teambot.communication.TBPositionData;
import teambot.pathplanning.Agent;
import teambot.simulator.SimulatorProxy;
import teambot.simulator.SimulatorProxy.PositionInfraredData;
import teambot.slam.Particle;
import teambot.slam.ParticleFilter;
import android.graphics.PointF;

public class PathPlanningAgentUpdater implements Runnable {

	public static final int offsetY = 10000;
	
	protected AtomicBoolean running = new AtomicBoolean(false);
	protected SimulatorProxy simulator;
	protected Agent agent;
	protected Position position;
	protected long timeBetweenUpdates;
	private ParticleFilter filter;
	private PositionSupplier positionSupplier = new PositionSupplier(new PointF(90.0f, 0));
	private IPositionListener positionListener = positionSupplier;
	private MapConverter converter;
	public AtomicBoolean wasUpdated = new AtomicBoolean(false);

	public PathPlanningAgentUpdater(SimulatorProxy proxy, Agent agent, Position position,
			ParticleFilter filter, MapConverter converter, int updateCycleInHz) {
		this.simulator = proxy;
		this.agent = agent;
		this.position = position;
		timeBetweenUpdates = (long) (1000 / updateCycleInHz);
		this.positionSupplier.register(filter, (int)filter.getBeamModel().getCellSize(), 5);
		this.filter = filter;
		this.converter = converter;
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
			
			float newAngle = Position.normalizeAngle_plusMinus180(-positionMeasurement.angle * 0.01f + 90f);
			
			position.setX(positionMeasurement.x);
			position.setY(offsetY - positionMeasurement.y);
			position.setAngleInDegree(newAngle);
			
			positionListener.callback_PositionChanged(position);
			filter.callback_NewMeasurement((infraredMeasurement.middleDistance & 0xFF) * 10);
			Particle bestParticle = filter.getBestParticle();
			simulator.sendDebugMap(converter.convertSlamMap(bestParticle.getMap(), bestParticle.getPosition()), (short) converter.getCellSize());
			
			if(wasUpdated.get() == false)
				wasUpdated.set(true);
			
			agent.addMeasurement(position.getPosition(), position.getAngleInRadian(), infraredMeasurement.leftDistance & 0xFF,
					infraredMeasurement.middleDistance & 0xFF, infraredMeasurement.rightDistance & 0xFF);
		}
	}

}
