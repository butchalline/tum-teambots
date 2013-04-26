package teambot.slam;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

import teambot.common.data.Position;
import teambot.pathplanning.Occupation;
import android.graphics.Point;

public class Particle
{

	Position _position;
	ProbabilityMap _map;
	BeamModel _beamModel;
	NoiseProvider _noiseProvider;
	float _slidingFactor;
	float _weight = 0.5f;
	static float _occupiedPointWeightMultiplier = 3;

	public Particle(Position position,
			ProbabilityMap map, BeamModel beamModel, NoiseProvider noise,
			float slidingFactor)
	{
		_position = position;
		_map = map;
		_beamModel = beamModel;
		_noiseProvider = noise;
		_slidingFactor = slidingFactor;
	}

	public Particle(Position position,
			ProbabilityMap map, BeamModel beamModel, NoiseProvider noise,
			float slidingFactor, float weight)
	{
		_position = position;
		_map = map;
		_beamModel = beamModel;
		_noiseProvider = noise;
		_slidingFactor = slidingFactor;
		_weight = weight;
	}

	public Particle(Particle particle) {
		_position = new Position(particle._position);
		_map = new ProbabilityMap(particle._map);
		_beamModel = new BeamModel(particle._beamModel);
		_noiseProvider = new NoiseProvider(particle._noiseProvider);
		_slidingFactor = particle._slidingFactor;
		_weight = particle._weight;
	}

	public synchronized void updatePosition(Position positionChange)
	{
		Position noisyPositionChange = _noiseProvider.makePositionNoisy(positionChange);
		float newX = _position.getX() + noisyPositionChange.getX();
		float newY = _position.getY() + noisyPositionChange.getY();
		float newAngle = Position.normalizeAngle_plusMinusPi(_position.getAngleInRadian() + noisyPositionChange.getAngleInRadian());
		_position = new Position(newX, newY, newAngle);
	}

	public float updateAndGetWeight(float distance_mm)
	{
		LinkedList<SimpleEntry<Point, Occupation>> measuredPoints = _beamModel
				.calculateBeam(distance_mm, _position);
		float newWeight = 1;
		Float pointProbability;
		
		for (SimpleEntry<Point, Occupation> pointOccupation : measuredPoints)
		{
			pointProbability = _map.getProbability(pointOccupation.getKey());
			
			if(pointProbability == null)
				continue;
			
			if (pointOccupation.getValue() == Occupation.free)
				newWeight = newWeight
						* (1 - pointProbability);
			else
				newWeight = newWeight
						* pointProbability * _occupiedPointWeightMultiplier;
		}

		_weight = _weight * (1 - _slidingFactor) + newWeight * _slidingFactor;
		return _weight;
	}
	
	public float getWeight()
	{
		return _weight;
	}

	public void updateMap(float distance_mm) {
		LinkedList<SimpleEntry<Point, Occupation>> measuredPoints = _beamModel
				.calculateBeam(distance_mm, _position);
		_map.update(measuredPoints);		
	}
	
	public Position getPosition()
	{
		return _position;
	}
	
	public ProbabilityMap getMap()
	{
		return _map;
	}

	public void setWeigth(float f) {
		// TODO Auto-generated method stub
		
	}
}
