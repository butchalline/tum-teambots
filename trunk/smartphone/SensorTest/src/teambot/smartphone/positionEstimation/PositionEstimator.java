package teambot.smartphone.positionEstimation;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import dataLogger.LogDistributionManager;

import android.hardware.SensorManager;
import android.util.FloatMath;
import teambot.smartphone.helper.Constants;
import teambot.smartphone.helper.ValueEstimator;
import teambot.smartphone.sensortest.SensorListener;
import teambotData.DataType;
import teambotData.FloatArrayData;
import teambotData.Info;

public class PositionEstimator implements Runnable {

	protected FloatArrayData currentPosition;
	protected float[] rotationMatrix = { 1, 0, 0, 0, 1, 0, 0, 0, 1 };
	protected SensorListener accelListener;
	protected SensorListener gyroListener;
	protected LogDistributionManager logger;

	public AtomicBoolean running = new AtomicBoolean(false);

	public PositionEstimator(SensorListener accelListener,
			SensorListener gyroListener, LogDistributionManager logger) {
		this.accelListener = accelListener;
		this.gyroListener = gyroListener;
		this.logger = logger;
	}

	@Override
	public void run() {
		ConcurrentLinkedQueue<FloatArrayData> currentAccelValues;
		ConcurrentLinkedQueue<FloatArrayData> currentGyroValues;

		FloatArrayData latestAccelValue;
		FloatArrayData latestGyroValue;

		float pastTime;

		currentPosition = new FloatArrayData(System.currentTimeMillis(),
				new float[] { 0, 0, 0 }, DataType.POSITION);
		running = new AtomicBoolean(true);

		while (running.get()) {
			currentAccelValues = new ConcurrentLinkedQueue<FloatArrayData>(
					accelListener.latestValues);
			currentGyroValues = new ConcurrentLinkedQueue<FloatArrayData>(
					gyroListener.latestValues);

			if (currentAccelValues.peek().getTimestamp() > currentGyroValues
					.peek().getTimestamp()) {
				latestAccelValue = currentAccelValues.remove();
				latestGyroValue = ValueEstimator.estimate(
						currentGyroValues.remove(), currentGyroValues.remove(),
						currentGyroValues.remove(),
						latestAccelValue.getTimestamp());
			} else {
				latestGyroValue = currentGyroValues.remove();
				latestAccelValue = ValueEstimator.estimate(
						currentAccelValues.remove(),
						currentAccelValues.remove(),
						currentAccelValues.remove(),
						latestGyroValue.getTimestamp());
			}

			pastTime = latestAccelValue.getTimestamp()
					- currentPosition.getTimestamp();

			if (pastTime <= 0)
			{
//				logger.log(new Info("past Time <= 0: " + pastTime));
				continue;
			}

			float[] rotationChangeMatrix = calculateRotationFromGyro(latestGyroValue);
			rotationMatrix = multiplyRotationMatrices(rotationMatrix,
					rotationChangeMatrix);
			float[] newPosition = calculatePositionOffset(rotationMatrix,
					latestAccelValue.getData());

			newPosition[0] += currentPosition.getData()[0];
			newPosition[1] += currentPosition.getData()[1];
			newPosition[2] += currentPosition.getData()[2];

			currentPosition = new FloatArrayData(
					latestAccelValue.getTimestamp(), newPosition,
					DataType.POSITION);
			logger.log(currentPosition);
		}
		logger.log(new Info("Tracking ended"));
	}

	protected float[] calculateRotationFromGyro(FloatArrayData gyroData) {

		float timeStep = (gyroData.getTimestamp() - currentPosition
				.getTimestamp()) * Constants.NanoSecsToSecs;
		// Axis of the rotation sample, not normalized yet.
		float axisX = gyroData.getData()[0];
		float axisY = gyroData.getData()[1];
		float axisZ = gyroData.getData()[2];

		float margnitude = FloatMath.sqrt(axisX * axisX + axisY * axisY + axisZ
				* axisZ);

		// float EPSILON = 0.000000001f;
		// // Normalize the rotation vector if it's big enough to get the axis
		// // (that is, EPSILON should represent your maximum allowable margin
		// // of error)
		// if (omegaMagnitude > EPSILON) {
		axisX /= margnitude;
		axisY /= margnitude;
		axisZ /= margnitude;
		// }

		float thetaOverTwo = margnitude * timeStep / 2.0f;
		float sinThetaOverTwo = FloatMath.sin(thetaOverTwo);
		float cosThetaOverTwo = FloatMath.cos(thetaOverTwo);

		float[] rotationChange = new float[4];
		rotationChange[0] = sinThetaOverTwo * axisX;
		rotationChange[1] = sinThetaOverTwo * axisY;
		rotationChange[2] = sinThetaOverTwo * axisZ;
		rotationChange[3] = cosThetaOverTwo;
		float[] rotationChangeMatrix = new float[9];
		SensorManager.getRotationMatrixFromVector(rotationChangeMatrix,
				rotationChange);
		return rotationChangeMatrix;
	}

	protected float[] calculatePositionOffset(float[] rotationMatrix,
			float[] offset) {

		float[] resultOffset = new float[3];

		resultOffset[0] = rotationMatrix[0] * offset[0];
		resultOffset[0] += rotationMatrix[1] * offset[1];
		resultOffset[0] += rotationMatrix[2] * offset[2];

		resultOffset[1] = rotationMatrix[3] * offset[0];
		resultOffset[1] += rotationMatrix[4] * offset[1];
		resultOffset[1] += rotationMatrix[5] * offset[2];

		resultOffset[2] = rotationMatrix[6] * offset[0];
		resultOffset[2] += rotationMatrix[7] * offset[1];
		resultOffset[2] += rotationMatrix[8] * offset[2];

		return resultOffset;
	}

	protected float[] multiplyRotationMatrices(float[] oldMatrix,
			float[] changeMatrix) {

		float[] newMatrix = new float[9];

		// TODO use opencv...
		newMatrix[0] = oldMatrix[0] * changeMatrix[0];
		newMatrix[0] += oldMatrix[1] * changeMatrix[3];
		newMatrix[0] += oldMatrix[2] * changeMatrix[6];

		newMatrix[1] = oldMatrix[0] * changeMatrix[1];
		newMatrix[1] += oldMatrix[1] * changeMatrix[4];
		newMatrix[1] += oldMatrix[2] * changeMatrix[7];

		newMatrix[2] = oldMatrix[0] * changeMatrix[2];
		newMatrix[2] += oldMatrix[1] * changeMatrix[5];
		newMatrix[2] += oldMatrix[2] * changeMatrix[8];

		newMatrix[3] = oldMatrix[3] * changeMatrix[0];
		newMatrix[3] += oldMatrix[4] * changeMatrix[3];
		newMatrix[3] += oldMatrix[5] * changeMatrix[6];

		newMatrix[4] = oldMatrix[3] * changeMatrix[1];
		newMatrix[4] += oldMatrix[4] * changeMatrix[4];
		newMatrix[4] += oldMatrix[5] * changeMatrix[7];

		newMatrix[5] = oldMatrix[3] * changeMatrix[2];
		newMatrix[5] += oldMatrix[4] * changeMatrix[5];
		newMatrix[5] += oldMatrix[5] * changeMatrix[8];

		newMatrix[6] = oldMatrix[6] * changeMatrix[0];
		newMatrix[6] += oldMatrix[7] * changeMatrix[3];
		newMatrix[6] += oldMatrix[8] * changeMatrix[6];

		newMatrix[7] = oldMatrix[6] * changeMatrix[1];
		newMatrix[7] += oldMatrix[7] * changeMatrix[4];
		newMatrix[7] += oldMatrix[8] * changeMatrix[7];

		newMatrix[8] = oldMatrix[6] * changeMatrix[2];
		newMatrix[8] += oldMatrix[7] * changeMatrix[5];
		newMatrix[8] += oldMatrix[8] * changeMatrix[8];

		return newMatrix;
	}

	protected synchronized void setPosition(FloatArrayData position) {
		currentPosition = position;
	}

	public synchronized FloatArrayData getPosition() {
		return currentPosition;
	}
}
