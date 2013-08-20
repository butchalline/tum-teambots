package teambot.common.slam;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

import org.apache.commons.math3.distribution.NormalDistribution;

import teambot.common.BotLayoutConstants;
import teambot.common.PoseSupplier;
import teambot.common.data.Pose;
import teambot.common.utils.MathHelper;
import teambot.common.slam.BeamModel;
import teambot.common.slam.NoiseProvider;
import teambot.common.slam.Occupation;
import teambot.common.slam.Particle;
import teambot.common.slam.ProbabilityMap;
import android.graphics.Point;
import android.graphics.PointF;

public class Particle
{

	protected Pose _pose;
	protected ProbabilityMap _map;
	protected BeamModel _beamModel;
	protected NoiseProvider _noiseProvider;
	protected float _weight = 1f;
	protected float _slidingFactor;
	protected int _weightUpdateCounter = 1;
	static float _occupiedPointWeightMultiplier = 3;
	static float _epsilon = Float.MIN_VALUE;
	static NormalDistribution normalDistributionZeroMean = new NormalDistribution(0, 180);

	public Particle(Pose pose, ProbabilityMap map, BeamModel beamModel, NoiseProvider noise, float slidingFactor)
	{
		_pose = pose;
		_map = map;
		_beamModel = beamModel;
		_noiseProvider = noise;
		_slidingFactor = slidingFactor;
	}

	public Particle(Pose pose, ProbabilityMap map, BeamModel beamModel, NoiseProvider noise,
			float slidingFactor, float weight)
	{
		_pose = pose;
		_map = map;
		_beamModel = beamModel;
		_noiseProvider = noise;
		_slidingFactor = slidingFactor;
		_weight = weight;
	}

	public Particle(Particle particle)
	{
		synchronized (particle)
		{
			_pose = new Pose(particle._pose);
			_map = new ProbabilityMap(particle._map);
			_beamModel = new BeamModel(particle._beamModel);
			_noiseProvider = new NoiseProvider(particle._noiseProvider);
			_weight = particle._weight;
			_slidingFactor = particle._slidingFactor;
			_weightUpdateCounter = particle._weightUpdateCounter;	
		}
	}

	public synchronized void updatePose(Pose poseChange)
	{
		Pose noisyPoseChange = _noiseProvider.makePositionChangeNoisy(poseChange);

		float newX = (float) (_pose.getX() + Math.cos(_pose.getAngleInRadian()) * noisyPoseChange.getX());
		float newY = (float) (_pose.getY() + Math.sin(_pose.getAngleInRadian()) * noisyPoseChange.getX());
		float newAngle = Pose.normalizeAngle_plusMinusPi(_pose.getAngleInRadian()
				+ noisyPoseChange.getAngleInRadian());
		_pose = new Pose(newX, newY, newAngle);
	}

	public float updateAndGetNewWeight(float measuredDistance_mm)
	{
		LinkedList<SimpleEntry<Point, Occupation>> measuredPoints = _beamModel.calculateBeamMaxRange(PoseSupplier
				.addOffset(_pose, BotLayoutConstants.distanceSensorOffset_mm));

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
				mapDistance_mm = MathHelper.calculateDistance(_pose.getPosition(),
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
				PoseSupplier.addOffset(_pose, BotLayoutConstants.distanceSensorOffset_mm));

		_map.update(measuredPoints);
		return newWeight;

	}

	public float getDistanceOnMap()
	{
		LinkedList<SimpleEntry<Point, Occupation>> measuredPoints = _beamModel.calculateBeamMaxRange(PoseSupplier
				.addOffset(_pose, BotLayoutConstants.distanceSensorOffset_mm));

		float mapDistance_mm = _beamModel.getMaxRange();
		Float pointProbability;

		for (SimpleEntry<Point, Occupation> pointOccupation : measuredPoints)
		{
			pointProbability = _map.getProbability(pointOccupation.getKey());

			if (pointProbability == null)
				continue;

			if (pointProbability > 0.5f)// _noiseProvider.getRandom())
			{
				mapDistance_mm = MathHelper.calculateDistance(_pose.getPosition(),
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

	public Pose getPose()
	{
		return _pose;
	}

	public ProbabilityMap getMap()
	{
		return _map;
	}

	public void setWeigth(float weight)
	{
		_weight = weight;
	}

	public void resetWeightCounter()
	{
		_weightUpdateCounter = 1;
	}
}
