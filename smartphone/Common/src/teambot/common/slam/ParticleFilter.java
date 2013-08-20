package teambot.common.slam;

import java.util.Random;
import java.util.Vector;

import teambot.common.data.Pose;
import teambot.common.hardware.SensorValue;
import teambot.common.interfaces.IPoseChangeListener;
import teambot.common.interfaces.ISensorListener;
import teambot.common.slam.BeamModel;
import teambot.common.slam.BeamProbabilities;
import teambot.common.slam.NoiseProvider;
import teambot.common.slam.Particle;
import teambot.common.slam.ProbabilityMap;
import android.graphics.PointF;

public class ParticleFilter implements IPoseChangeListener, ISensorListener
{
	private Random random = new Random(System.currentTimeMillis());
	private int _particleAmount;
	private float _nThresh;
	private static float _nThresholdDivider = 2f;

	protected Particle[] _particles;
	protected Pose _latestPose = null;
//	protected HistogramViewer histogramViewer;

	float _slidingFactor = 0.01f;
	BeamModel _beamModel;

	public ParticleFilter(float cellSize_mm, float maxRange_mm, float p0, float pOccupation, float pFree,
			NoiseProvider noiser, int particleAmount, Pose startPose)
	{
		_particleAmount = particleAmount;
		_nThresh = _particleAmount / _nThresholdDivider;
		_particles = new Particle[_particleAmount];

//		histogramViewer = new HistogramViewer("Particle Histogram", 3.0f / _particleAmount);

		BeamProbabilities rayProbability = new BeamProbabilities(p0, pOccupation, pFree);
		_beamModel = new BeamModel(cellSize_mm, maxRange_mm);

		for (int i = 0; i < _particles.length; ++i)
		{
			Pose particleStartPose = new Pose(startPose);
			ProbabilityMap probMap = new ProbabilityMap(rayProbability);
			_particles[i] = new Particle(particleStartPose, probMap, _beamModel, noiser, _slidingFactor);
		}
	}

	@Override
	public void poseChangeCallback(Pose poseChange)
	{
		for (Particle particle : _particles)
		{
			particle.updatePose(poseChange);
		}
	}
	
	@Override
	public void newSensorValueCallback(SensorValue distance_cm)
	{
		float totalWeight = 0;
		float heighestNewWeight = 0;
		float tempWeight = 0;

		Vector<Particle> outOfRangeParticles = new Vector<Particle>();

		for (Particle particle : _particles)
		{
			tempWeight = particle.updateAndGetNewWeight(distance_cm.value() * 10f);

			if (tempWeight < 0)
			{
				outOfRangeParticles.add(particle);
				continue;
			}

			if (tempWeight > heighestNewWeight)
				heighestNewWeight = tempWeight;

			totalWeight += particle.getWeight();
		}

		if (heighestNewWeight == 0)
			heighestNewWeight = 1;

		for (Particle particle : outOfRangeParticles)
		{
			particle.setWeigth(particle.getWeight() * heighestNewWeight);
			totalWeight += particle.getWeight();
		}

//		Particle bestParticle = getBestParticle();
//		float distanceBest = bestParticle.getDistanceOnMap();
//		System.out.println("Distance diff: " + (distance_mm - distanceBest) + "; Map: " + distanceBest);

		// check if re-sampling is needed
		float invNeff = 0;
		for (Particle particle : _particles)
		{
			particle.setWeigth(particle.getWeight() / totalWeight);
			invNeff += (particle.getWeight() * particle.getWeight());
		}

		if (1 / invNeff < _nThresh)
		{
			// printParticlesInfo();
			 System.out.println("Resampling");
			this.resample(1);
		}
	}

	protected void resample(float totalWeight)
	{
		float[] weightArray = new float[_particles.length];

		shuffleArray(_particles);

		// sort weights, kind of like a dart board
		weightArray[0] = _particles[0].getWeight() / totalWeight;
		for (int i = 1; i < _particles.length; i++)
		{
			weightArray[i] = weightArray[i - 1] + _particles[i].getWeight() / totalWeight;
		}
		weightArray[weightArray.length - 1] = 1;

		// Generate random numbers (maybe not perfectly working)
		float[] randomSortedArray = new float[_particles.length];
		for (int i = 0; i < _particles.length; i++)
		{
			randomSortedArray[i] = (i + random.nextFloat()) / _particles.length;
		}

		// re-sample
		Particle[] newParticles = new Particle[_particles.length];
		Particle actualParticle = _particles[0];
		actualParticle.setWeigth(1.0f / _particleAmount);
		actualParticle.resetWeightCounter();
		int newParticleIndex = 0;
		int actualParticleIndex = 0;

		while (newParticleIndex != _particles.length)
		{
			if (randomSortedArray[newParticleIndex] <= weightArray[actualParticleIndex])
			{
				newParticles[newParticleIndex] = new Particle(actualParticle);
				newParticleIndex += 1;
			} else if (randomSortedArray[newParticleIndex] > weightArray[actualParticleIndex])
			{
				actualParticleIndex += 1;
				actualParticle = _particles[actualParticleIndex];
				actualParticle.setWeigth(totalWeight / _particleAmount);
				actualParticle.resetWeightCounter();
			}
		}

		_particles = newParticles;
	}

	void shuffleArray(Particle[] _particles2)
	{
		Random rnd = new Random();
		for (int i = _particles2.length - 1; i >= 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			// Simple swap
			Particle tempParticle = _particles2[index];
			_particles2[index] = _particles2[i];
			_particles2[i] = tempParticle;
		}
	}

	void updateParticleHistogram()
	{
		double[] values = new double[_particles.length];

		for (int i = 0; i < _particles.length; ++i)
			values[i] = _particles[i].getWeight();

//		histogramViewer.updateHistogram(values);
	}
	public Particle getBestParticle()
	{
		Particle bestParticle = _particles[0];
		for (Particle particle : _particles)
		{
			if (particle.getWeight() > bestParticle.getWeight())
				bestParticle = particle;
		}
		return bestParticle;
	}

	public PointF calculateParticleMeanPosition(Particle[] particles)
	{
		PointF mean = new PointF(0, 0);

		for (Particle particle : particles)
		{
			mean.x += particle.getPose().getX();
			mean.y += particle.getPose().getY();
		}

		return new PointF(mean.x / particles.length, mean.y / particles.length);
	}

	PointF calculateParticleSDeviationPosition(Particle[] particles)
	{
		PointF mean = calculateParticleMeanPosition(particles);
		return calculateParticleSDeviationPosition(particles, mean);
	}

	PointF calculateParticleSDeviationPosition(Particle[] particles, PointF mean)
	{
		PointF deviation = new PointF(0, 0);

		for (Particle particle : particles)
		{
			deviation.x += (particle.getPose().getX() - mean.x) * (particle.getPose().getX() - mean.x);
			deviation.y += (particle.getPose().getY() - mean.y) * (particle.getPose().getY() - mean.y);
		}

		deviation.x = (float) Math.sqrt(deviation.x / particles.length);
		deviation.y = (float) Math.sqrt(deviation.y / particles.length);

		return deviation;
	}

	public synchronized BeamModel getBeamModel()
	{
		return _beamModel;
	}

	public synchronized Particle[] getParticlesCopy()
	{
		Particle[] particlesCopy = new Particle[_particleAmount];
		System.arraycopy(_particles, 0, particlesCopy, 0, _particleAmount);
		return particlesCopy;
	}
}
