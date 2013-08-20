package teambot.common.slam;

import java.util.Random;

import teambot.common.data.Pose;
import teambot.common.slam.NoiseProvider;

public class NoiseProvider {
	Random rand = new Random();
	float _varianceConstPosition;
	float _varianceConstY;
	float _varianceConstAngle_rad;
	float _variancePropPosition;
	float _variancePropY;
	float _variancePropAngle_rad;
	
	public NoiseProvider(float varianceConstX, float varianceConstY, float varianceConstAngle_deg, 
			float variancePropX, float variancePropY, float variancePropAngle_deg){	
		_varianceConstPosition = varianceConstX;
		_varianceConstY = varianceConstY;
		_varianceConstAngle_rad = varianceConstAngle_deg;
		_variancePropPosition = variancePropX;
		_variancePropY = variancePropY;
		_variancePropAngle_rad = variancePropAngle_deg;
	}
	
	public NoiseProvider(NoiseProvider noise){	
		_varianceConstPosition = noise._varianceConstPosition;
		_varianceConstY = noise._varianceConstY;
		_varianceConstAngle_rad = noise._varianceConstAngle_rad;
		_variancePropPosition = noise._variancePropPosition;
		_variancePropY = noise._variancePropY;
		_variancePropAngle_rad = noise._variancePropAngle_rad;
	}
	
	public Pose makePositionChangeNoisy(Pose poseChange)
	{
		float noisyChangeX = poseChange.getX() * (float) (1 + rand.nextGaussian() * _variancePropPosition) + (float) rand.nextGaussian() * _varianceConstPosition;
		float noisyChangeY = poseChange.getY() * (float) (1 + rand.nextGaussian() * _variancePropPosition) + (float) rand.nextGaussian() * _varianceConstPosition;
		float noisyChangeAngle = poseChange.getAngleInRadian() * (float) (1 + rand.nextGaussian() * _variancePropPosition) + (float) rand.nextGaussian() * _varianceConstPosition;
		
		return new Pose(noisyChangeX, noisyChangeY, noisyChangeAngle);
	}
	
	public float getRandom()
	{
		return rand.nextFloat();
	}
}
