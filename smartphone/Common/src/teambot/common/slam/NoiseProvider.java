package teambot.common.slam;

import java.util.Random;

import teambot.common.data.Pose;
import teambot.common.slam.NoiseProvider;
import teambot.common.utils.Constants;

public class NoiseProvider
{
	Random rand = new Random();
	float _varianceConstPosition;
	float _varianceConstY;
	float _varianceConstAngle_deg;
	float _variancePropPosition;
	float _variancePropY;
	float _variancePropAngle;

	public NoiseProvider(float varianceConstX, float varianceConstY, float varianceConstAngle_deg, float variancePropX,
			float variancePropY, float variancePropAngle)
	{
		_varianceConstPosition = varianceConstX;
		_varianceConstY = varianceConstY;
		_varianceConstAngle_deg = varianceConstAngle_deg;
		_variancePropPosition = variancePropX;
		_variancePropY = variancePropY;
		_variancePropAngle = variancePropAngle;
	}

	public NoiseProvider(NoiseProvider noise)
	{
		_varianceConstPosition = noise._varianceConstPosition;
		_varianceConstY = noise._varianceConstY;
		_varianceConstAngle_deg = noise._varianceConstAngle_deg;
		_variancePropPosition = noise._variancePropPosition;
		_variancePropY = noise._variancePropY;
		_variancePropAngle = noise._variancePropAngle;
	}

	public Pose makePositionChangeNoisy(Pose poseChange)
	{
		float noisyChangeX = poseChange.getX() * (float) (1 + rand.nextGaussian() * _variancePropPosition)
				+ (float) rand.nextGaussian() * _varianceConstPosition;
		float noisyChangeY = poseChange.getY() * (float) (1 + rand.nextGaussian() * _variancePropPosition)
				+ (float) rand.nextGaussian() * _varianceConstPosition;
		float noisyChangeAngle = poseChange.getAngleInRadian() * (float) (1 + rand.nextGaussian() * _variancePropAngle)
				+ (float) rand.nextGaussian() * _varianceConstAngle_deg * Constants.DegreeToRadian;

		return new Pose(noisyChangeX, noisyChangeY, noisyChangeAngle);
	}

	public float getRandom()
	{
		return rand.nextFloat();
	}
}
