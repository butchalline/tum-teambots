package teambot.slam;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

import teambot.common.PositionSupplier;
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
	static float _epsilon = Float.MIN_VALUE;

	public Particle(Position position, ProbabilityMap map, BeamModel beamModel, NoiseProvider noise, float slidingFactor)
	{
		_position = position;
		_map = map;
		_beamModel = beamModel;
		_noiseProvider = noise;
		_slidingFactor = slidingFactor;
	}

	public Particle(Position position, ProbabilityMap map, BeamModel beamModel, NoiseProvider noise,
			float slidingFactor, float weight)
	{
		_position = position;
		_map = map;
		_beamModel = beamModel;
		_noiseProvider = noise;
		_slidingFactor = slidingFactor;
		_weight = weight;
	}

	public Particle(Particle particle)
	{
		_position = new Position(particle._position);
		_map = new ProbabilityMap(particle._map);
		_beamModel = new BeamModel(particle._beamModel);
		_noiseProvider = new NoiseProvider(particle._noiseProvider);
		_slidingFactor = particle._slidingFactor;
		_weight = particle._weight;
	}

	public synchronized void updatePosition(float positionChange, float angleChange_rad)
	{
		Position noisyPositionChange = _noiseProvider.makePositionChangeNoisy(positionChange, angleChange_rad);

		float newX = (float) (_position.getX() + Math.cos(_position.getAngleInRadian()) * noisyPositionChange.getX());
		float newY = (float) (_position.getY() + Math.sin(_position.getAngleInRadian()) * noisyPositionChange.getX());
		float newAngle = Position.normalizeAngle_plusMinusPi(_position.getAngleInRadian()
				+ noisyPositionChange.getAngleInRadian());
		_position = new Position(newX, newY, newAngle);
	}

	public synchronized void setStartPosition(Position startPosition)
	{
		_position = new Position(startPosition);
	}

	public float updateAndGetWeight(float distance_mm)
	{
		LinkedList<SimpleEntry<Point, Occupation>> measuredPoints = _beamModel.calculateBeam(distance_mm,
				PositionSupplier.addOffset(_position, new Position(0.0f, 0f, 0f)));
		float newWeight = measuredPoints.size();
		Float pointProbability;

		for (SimpleEntry<Point, Occupation> pointOccupation : measuredPoints)
		{
			pointProbability = _map.getProbability(pointOccupation.getKey());

			if (pointProbability == null)
				continue;

			if ((pointOccupation.getValue() == Occupation.free && pointProbability < 0.5f)
					|| (pointOccupation.getValue() == Occupation.occupied && pointProbability > 0.5f))
				newWeight++;
			else
				newWeight--;
		}
		
		_weight = _weight * (1.0f - _slidingFactor) + newWeight * _slidingFactor;
		return _weight;

	}

	public float getWeight()
	{
		return _weight;
	}

	public void updateMap(float distance_mm)
	{
		LinkedList<SimpleEntry<Point, Occupation>> measuredPoints = _beamModel.calculateBeam(distance_mm,
				PositionSupplier.addOffset(_position, new Position(0.0f, 0f, 0f)));
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

	public void setWeigth(float f)
	{
		// TODO Auto-generated method stub

	}
}
