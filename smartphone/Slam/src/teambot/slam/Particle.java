package teambot.slam;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

import javax.sql.CommonDataSource;

import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.MatrixUtils;

import teambot.common.PositionSupplier;
import teambot.common.data.Position;
import teambot.common.utils.MathHelper;
import teambot.pathplanning.Occupation;
import android.graphics.Point;
import android.graphics.PointF;

public class Particle
{

	protected Position _position;
	protected ProbabilityMap _map;
	protected BeamModel _beamModel;
	protected NoiseProvider _noiseProvider;
	protected float _weight = 1f;
	protected float _slidingFactor;
	protected int _weightUpdateCounter = 1;
	static float _occupiedPointWeightMultiplier = 3;
	static float _epsilon = Float.MIN_VALUE;
	static NormalDistribution normalDistributionZeroMean = new NormalDistribution(0, 180);

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
		_weight = particle._weight;
		_slidingFactor = particle._slidingFactor;
		_weightUpdateCounter = particle._weightUpdateCounter;
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

	public float updateAndGetNewWeight(float measuredDistance_mm)
	{
		LinkedList<SimpleEntry<Point, Occupation>> measuredPoints = _beamModel.calculateBeamMaxRange(PositionSupplier
				.addOffset(_position, new Position(0.0f, 0f, 0f)));

		float newWeight = 1;
		float mapDistance_mm = -1;
		Float pointProbability;

		for (SimpleEntry<Point, Occupation> pointOccupation : measuredPoints)
		{
			pointProbability = _map.getProbability(pointOccupation.getKey());

			if (pointProbability == null)
				continue;

			if (pointProbability > 0.5f)// _noiseProvider.getRandom())
			{
				mapDistance_mm = MathHelper.calculateDistance(_position.getPosition(),
						new PointF(pointOccupation.getKey().x * _beamModel.getCellSize(), pointOccupation.getKey().y
								* _beamModel.getCellSize()));
				break;
			}
		}

		if (mapDistance_mm != -1)
		{

			if (Math.abs(mapDistance_mm - measuredDistance_mm) < _beamModel.getCellSize() * 0.5f)
				newWeight = (float) normalDistributionZeroMean.density(_beamModel.getCellSize() * 0.5f);
			else
				newWeight = (float) normalDistributionZeroMean.density(mapDistance_mm - measuredDistance_mm);

			_weightUpdateCounter++;

			_weight = _weight * newWeight;
			// _weight = _weight * (1 - _slidingFactor) + newWeight *
			// _slidingFactor;
			// _weight = _weight * (1 - (1 / _weightUpdateCounter)) + newWeight
			// / _weightUpdateCounter;
		} else
			newWeight = -1;

		measuredPoints = _beamModel.calculateBeam(measuredDistance_mm,
				PositionSupplier.addOffset(_position, new Position(0.0f, 0f, 0f)));

		_map.update(measuredPoints);
		return newWeight;

	}

	public float getDistanceOnMap()
	{
		LinkedList<SimpleEntry<Point, Occupation>> measuredPoints = _beamModel.calculateBeamMaxRange(PositionSupplier
				.addOffset(_position, new Position(0.0f, 0f, 0f)));

		float newWeight = 1;
		float mapDistance_mm = _beamModel.getMaxRange();
		Float pointProbability;

		for (SimpleEntry<Point, Occupation> pointOccupation : measuredPoints)
		{
			pointProbability = _map.getProbability(pointOccupation.getKey());

			if (pointProbability == null)
				continue;

			if (pointProbability > 0.5f)// _noiseProvider.getRandom())
			{
				mapDistance_mm = MathHelper.calculateDistance(_position.getPosition(),
						new PointF(pointOccupation.getKey().x * _beamModel.getCellSize(), pointOccupation.getKey().y
								* _beamModel.getCellSize()));
				break;
			}
		}

		return mapDistance_mm;
	}

	public float getWeight()
	{
		return _weight;
	}

	public Position getPosition()
	{
		return _position;
	}

	public ProbabilityMap getMap()
	{
		return _map;
	}

	public void setWeigth(float weight)
	{
		int k = 0;
		if (new Float(weight).isNaN() || new Float(weight).isInfinite())
			k++;
		_weight = weight;
	}

	public void resetWeightCounter()
	{
		_weightUpdateCounter = 1;
	}
}
