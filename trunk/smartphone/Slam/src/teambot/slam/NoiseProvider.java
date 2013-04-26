package teambot.slam;

import java.util.Random;

import teambot.common.data.Position;
import teambot.common.utils.Constants;

public class NoiseProvider {
	Random rand = new Random();
	float _varianceConstX;
	float _varianceConstY;
	float _varianceConstAngle_rad;
	float _variancePropX;
	float _variancePropY;
	float _variancePropAngle_rad;
	
	public NoiseProvider(float varianceConstX, float varianceConstY, float varianceConstAngle_deg, 
			float variancePropX, float variancePropY, float variancePropAngle_deg){	
		_varianceConstX = varianceConstX;
		_varianceConstY = varianceConstY;
		_varianceConstAngle_rad = varianceConstAngle_deg * Constants.DegreeToRadian;
		_variancePropX = variancePropX;
		_variancePropY = variancePropY;
		_variancePropAngle_rad = variancePropAngle_deg * Constants.DegreeToRadian;
	}
	
	public NoiseProvider(NoiseProvider noise){	
		_varianceConstX = noise._varianceConstX;
		_varianceConstY = noise._varianceConstY;
		_varianceConstAngle_rad = noise._varianceConstAngle_rad;
		_variancePropX = noise._variancePropX;
		_variancePropY = noise._variancePropY;
		_variancePropAngle_rad = noise._variancePropAngle_rad;
	}
	
	public Position makePositionNoisy(Position positionChange)
	{
		float changeDistance = (float) Math.sqrt(positionChange.getX() * positionChange.getX() + positionChange.getY() * positionChange.getY());
		float changeX_botDirection = (float) Math.cos(positionChange.getAngleInRadian()) * changeDistance;
		float changeY_botDirection = (float) Math.sin(positionChange.getAngleInRadian()) * changeDistance;

		float noiseX = changeX_botDirection * (float) rand.nextGaussian() * _variancePropX + (float) rand.nextGaussian() * _varianceConstX;
		float noiseY = changeY_botDirection * (float) rand.nextGaussian() * _variancePropY + (float) rand.nextGaussian() * _varianceConstY; 
		float noiseAngle_rad = positionChange.getAngleInRadian() * (float) rand.nextGaussian() * _variancePropAngle_rad + (float) rand.nextGaussian() * _varianceConstAngle_rad; 
		
		return new Position(positionChange.getX() + noiseX, positionChange.getY() + noiseY, positionChange.getAngleInRadian() + noiseAngle_rad);
	}
}
