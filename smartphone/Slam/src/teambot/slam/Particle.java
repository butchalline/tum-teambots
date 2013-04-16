package teambot.slam;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

import teambot.common.data.PositionOrientation;
import teambot.pathplanning.Occupation;
import android.graphics.Point;

public class Particle
{

	PositionOrientation _positionOrientation;
	ProbabilityMap _map;
	BeamModel _beamModel;
	NoiseProvider _noiseProvider;
	float _slidingFactor;
	float _weight = 0.5f;

	public Particle(PositionOrientation positionOrientation,
			ProbabilityMap map, BeamModel beamModel, NoiseProvider noise,
			float slidingFactor)
	{
		_positionOrientation = positionOrientation;
		_map = map;
		_beamModel = beamModel;
		_noiseProvider = noise;
		_slidingFactor = slidingFactor;
	}

	public Particle(PositionOrientation positionOrientation,
			ProbabilityMap map, BeamModel beamModel, NoiseProvider noise,
			float slidingFactor, float weight)
	{
		_positionOrientation = positionOrientation;
		_map = map;
		_beamModel = beamModel;
		_noiseProvider = noise;
		_slidingFactor = slidingFactor;
		_weight = weight;
	}

	public synchronized void updatePositionOrientation(PositionOrientation newPositionOrientation)
	{
		PositionOrientation noisyPositionOrientation = new PositionOrientation(newPositionOrientation.getX() + _noiseProvider.noiseX(),
				newPositionOrientation.getY() + _noiseProvider.noiseY(), 
				newPositionOrientation.getAngleInRadian() + _noiseProvider.noiseAngle()); 
		_positionOrientation = noisyPositionOrientation;
	}

	public float getAndUpdateWeight(float distance_mm)
	{
		LinkedList<SimpleEntry<Point, Occupation>> measuredPoints = _beamModel
				.calculateBeam(distance_mm, _positionOrientation);
		float newWeight = 1;

		for (SimpleEntry<Point, Occupation> pointOccupation : measuredPoints)
		{
			if (pointOccupation.getValue() == Occupation.free)
				newWeight = newWeight
						* (1 - _map.getProbability(pointOccupation.getKey()));
			else
				newWeight = newWeight
						* _map.getProbability(pointOccupation.getKey());
		}

		_weight = _weight * (1 - _slidingFactor) + newWeight * _slidingFactor;
		return _weight;
	}
	
	public float getWeight()
	{
		return _weight;
	}

	public void updateMap(LinkedList<SimpleEntry<Point, Occupation>> points)
	{
		_map.updateMap(points);
	}
}
