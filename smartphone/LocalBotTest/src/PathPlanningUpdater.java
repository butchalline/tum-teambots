import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import teambot.common.data.Pose;
import teambot.common.usb.UsbHeader;
import teambot.common.utils.Constants;
import teambot.common.utils.MathHelper;
import teambot.common.utils.ThreadUtil;
import teambot.common.utils.TimestampHelper;
import teambot.communication.TBFrame;
import teambot.communication.TBPosition;
import teambot.pathplanning.Agent;
import teambot.simulator.SimulatorProxy;
import android.graphics.PointF;

public class PathPlanningUpdater implements Runnable {

	protected AtomicBoolean running = new AtomicBoolean(false);
	protected SimulatorProxy simulator;
	protected Agent _agent;
	protected Pose _pose;
	PathPlanningAgentUpdater _agentUpdater;
	protected PointF targetPoint;
	

	public PathPlanningUpdater(SimulatorProxy proxy, Agent agent, Pose pose, PathPlanningAgentUpdater agentUpdater) {
		simulator = proxy;
		_agent = agent;
		_pose = pose;
		_agentUpdater = agentUpdater;
	}

	@Override
	public void run() {
		running.set(true);

		int walkedFieldsCount = 0;
		ArrayDeque<PointF> path = new ArrayDeque<PointF>(100);
		float angleChange;
		short angleChangeAsShort = 0;
		float distance;
		@SuppressWarnings("unused")
		float targetAngle = 0;

		// change to position drive
		simulator.send(new TBFrame(UsbHeader.TB_COMMAND_REQUESTSTATE_POSITIONDRIVE.getId(), UsbHeader.TB_COMMAND_REQUESTSTATE_POSITIONDRIVE
				.getSubId(), TimestampHelper.frameTimestampNow()));
		new Thread(_agentUpdater).start();
		ThreadUtil.sleepSecs(1);

		// rotate360();

		while (running.get()) {

			if (!_agentUpdater._wasUpdated.get()) {
				ThreadUtil.sleepSecs(0.1f);
				continue;
			}

			if (path.isEmpty() || walkedFieldsCount >= 15) {
				rotate360();
				path = _agent.makePath();
				walkedFieldsCount = 0;
//				simulator.sendDebugMap(converter.convertMap(agent.getMap()), (short)cellSize_mm);
			}

			if (path == null) {
				rotate360();
				path = new ArrayDeque<PointF>(100);
				continue;
			}
			//System.out.println("------------Loop start---------------");
			//System.out.println("walkedFieldsCount: " + walkedFieldsCount);
			//System.out.println("path size: " + path.size());


			targetPoint = path.pollFirst();
			//System.out.println("current pos: " + position.getX() + " x " + position.getY() + "; a: " + position.getAngleInDegree());
			//System.out.println("targetPoint: " + targetPoint.x + " - " + targetPoint.y);
			angleChange = calcRotation(targetPoint);
			targetAngle = angleChange + _pose.getAngleInRadian();
			distance = MathHelper.calculateDistance(targetPoint, _pose.getPosition());
			//System.out.println("targetAngle: " + Constants.RadianToDegree * targetAngle);
			//System.out.println("distance: " + distance);

			TBPosition rotationFrame = null;
			if (angleChange > 0) {
				angleChangeAsShort = (short) (Constants.RadianToDegree * angleChange * 100);
				rotationFrame = new TBPosition(UsbHeader.TB_POSITION_TURN_LEFT.getId(), UsbHeader.TB_POSITION_TURN_LEFT.getSubId(),
						TimestampHelper.frameTimestampNow(), angleChangeAsShort);
			} else {
				if (angleChange < 0) {
					angleChangeAsShort = (short) (Constants.RadianToDegree * -angleChange * 100);
					rotationFrame = new TBPosition(UsbHeader.TB_POSITION_TURN_RIGHT.getId(), UsbHeader.TB_POSITION_TURN_RIGHT.getSubId(),
							TimestampHelper.frameTimestampNow(), angleChangeAsShort);
				}
			}

			if (rotationFrame != null) {
				simulator.send(rotationFrame);
//				System.out.println("Rotate frame sent, angleChangeAsShort: " + (int) angleChangeAsShort);
				waitForPositionReached();
			}

			simulator.send(new TBPosition(UsbHeader.TB_POSITION_FORWARD.getId(), UsbHeader.TB_POSITION_FORWARD.getSubId(), TimestampHelper
					.frameTimestampNow(), (short) distance));
//			System.out.println("Move forward frame sent, distance (mm): " + distance);
			waitForPositionReached();

			//System.out.println("targetAngle: " + Constants.RadianToDegree * targetAngle);
//			System.out.println("end position: " + _pose.getX() + " x " + _pose.getY() + ", a: " + _pose.getAngleInDegree());
			walkedFieldsCount++;
		}

		// change back to velocity drive
		simulator.send(new TBFrame(UsbHeader.TB_COMMAND_REQUESTSTATE_VELOCITYDRIVE.getId(), UsbHeader.TB_COMMAND_REQUESTSTATE_VELOCITYDRIVE
				.getSubId(), TimestampHelper.frameTimestampNow()));

	}

	protected void rotate360() {
		simulator.send(new TBPosition(UsbHeader.TB_POSITION_TURN_RIGHT.getId(), UsbHeader.TB_POSITION_TURN_RIGHT.getSubId(),
				TimestampHelper.frameTimestampNow(), (short) (180 * 100)));
//		System.out.println("rotation 180 sended");
		waitForPositionReached();
		simulator.send(new TBPosition(UsbHeader.TB_POSITION_TURN_RIGHT.getId(), UsbHeader.TB_POSITION_TURN_RIGHT.getSubId(),
				TimestampHelper.frameTimestampNow(), (short) (180 * 100)));
//		System.out.println("rotation 180 sended");
		waitForPositionReached();
	}

	protected float calcRotation(PointF newPosition) {
		float targetAngle = (float) Math.atan2(newPosition.y - _pose.getY(), newPosition.x - _pose.getX());
		float angleDiff = targetAngle - _pose.getAngleInRadian();

		if (angleDiff > Constants.piAsFloat)
			angleDiff -= 2 * Constants.piAsFloat;
		else if (angleDiff <= -Constants.piAsFloat)
			angleDiff += 2 * Constants.piAsFloat;

		return angleDiff;
	}

	private void waitForPositionReached() {
		int k = 0;
		while (!simulator.positionIsReached()) {
			ThreadUtil.sleepSecs(0.01f);
			if (k == 0 || k % 100 == 0) {
				//System.out.println("waiting for position reached..");
//				System.out
//						.println("current position: " + position.getX() + " x " + position.getY() + ", a: " + position.getAngleInDegree());

//				if (targetPoint != null)
//					System.out.println("targetPoint: " + targetPoint.x + " - " + targetPoint.y);
			}
			k++;
		}

//		if (position.getAngleInDegree() < -180)
//			System.out.println("Position reached : " + position.getX() + " x " + position.getY() + ", a: " + position.getAngleInDegree());
//		System.out.println("Position reached : " + _pose.getX() + " x " + _pose.getY() + ", a: " + _pose.getAngleInDegree());
	}
}
