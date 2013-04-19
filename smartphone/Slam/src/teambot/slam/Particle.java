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

	public synchronized void updatePosition(Position newPosition)
	{
		Position noisyPosition = new Position(newPosition.getX() + _noiseProvider.noiseX(),
				newPosition.getY() + _noiseProvider.noiseY(), 
				newPosition.getAngleInRadian() + _noiseProvider.noiseAngle()); 
		_position = noisyPosition;
	}

	public float updateAndGetWeight(float distance_mm)
	{
		LinkedList<SimpleEntry<Point, Occupation>> measuredPoints = _beamModel
				.calculateBeam(distance_mm, _position);
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
