package teambot.slam;

import teambot.common.data.Position;
import teambot.common.interfaces.IDistanceListener;
import teambot.common.interfaces.IPositionListener;
import teambot.visualizer.HistogramViewer;

import java.awt.List;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.lang3.ArrayUtils;

import android.graphics.PointF;

public class ParticleFilter implements IPositionListener, IDistanceListener
{
	private Random random = new Random(System.currentTimeMillis());
	private int _particleAmount;
	private float _nThresh;
	private static float _nThresholdDivider = 2f;

	protected Particle[] _particles;
	protected Position _latestPosition = null;
	protected HistogramViewer histogramViewer;

	float _slidingFactor = 0.01f;
	BeamModel _beamModel;

	public ParticleFilter(float cellSize_mm, float maxRange_mm, float p0, float pOccupation, float pFree,
			NoiseProvider noiser, int particleAmount)
	{
		_particleAmount = particleAmount;
		_nThresh = _particleAmount / _nThresholdDivider;
		_particles = new Particle[_particleAmount];

		histogramViewer = new HistogramViewer("Particle Histogram", 3.0f / _particleAmount);

		BeamProbabilities rayProbability = new BeamProbabilities(p0, pOccupation, pFree);
		_beamModel = new BeamModel(cellSize_mm, maxRange_mm);

		for (int i = 0; i < _particles.length; ++i)
		{
			Position posOr = new Position(0, 0, 0);
			ProbabilityMap probMap = new ProbabilityMap(rayProbability);
			_particles[i] = new Particle(posOr, probMap, _beamModel, noiser, _slidingFactor);
		}
	}

	@Override
	public void callback_PositionChanged(Position newPosition)
	{
		if (_latestPosition == null)
		{
			_latestPosition = newPosition;
			for (Particle particle : _particles)
			{
				particle.setStartPosition(newPosition);
			}
			return;
		}

		Position absolutePositionChange = new Position(newPosition.getX() - _latestPosition.getX(), newPosition.getY()
				- _latestPosition.getY(), newPosition.getAngleInRadian() - _latestPosition.getAngleInRadian());
		float positionChange = (float) Math.sqrt(absolutePositionChange.getX() * absolutePositionChange.getX()
				+ absolutePositionChange.getY() * absolutePositionChange.getY());
		_latestPosition = newPosition;

		for (Particle particle : _particles)
		{
			particle.updatePosition(positionChange, absolutePositionChange.getAngleInRadian());
		}
	}

	@Override
	public void callback_NewMeasurement(float distance_mm)
	{
		float totalWeight = 0;
		float heighestNewWeight = 0;
		float tempWeight = 0;

		Vector<Particle> outOfRangeParticles = new Vector<Particle>();

		for (Particle particle : _particles)
		{
			tempWeight = particle.updateAndGetNewWeight(distance_mm);

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

		Particle bestParticle = getBestParticle();

		float distanceBest = bestParticle.getDistanceOnMap();

//		System.out.println("Distance diff: " + (distance_mm - distanceBest) + "; Map: " + distanceBest);

		// check if re-sampling is needed
		float invNeff = 0;
		for (Particle particle : _particles)
		{
			particle.setWeigth(particle.getWeight() / totalWeight);
			invNeff += (particle.getWeight() * particle.getWeight());
		}

		float meanPositionX = 0;
		float meanPositionY = 0;

		for (Particle particle : _particles)
		{
			meanPositionX += particle.getPosition().getX();
			meanPositionY += particle.getPosition().getY();
		}

		meanPositionX = meanPositionX / _particles.length;
		meanPositionY = meanPositionY / _particles.length;

		float positionDeviationX = 0;
		float positionDeviationY = 0;
		for (Particle particle : _particles)
		{
			positionDeviationX += (meanPositionX - particle.getPosition().getX())
					* (meanPositionX - particle.getPosition().getX());
			positionDeviationY += (meanPositionY - particle.getPosition().getY())
					* (meanPositionY - particle.getPosition().getY());
		}

		updateParticleHistogram();

		float positionDeviation = (float) Math.sqrt((positionDeviationX + positionDeviationY)
				* (positionDeviationX + positionDeviationY));

		if (1 / invNeff < _nThresh)// || positionDeviation > 10 * 1000 * 1000)
		{
			// printParticlesInfo();
			// System.out.println("positionDeviation = " + positionDeviation);
			// System.out.println("1/invNeff = " + (1 / invNeff) + "; nThres = "
			// + _nThresh);
			// System.out.println("Resampling");
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
		float randomSum = 0;
		for (int i = 0; i < _particles.length; i++)
		{
			randomSortedArray[i] = (i + random.nextFloat()) / _particles.length;
			// randomSortedArray[i] = random.nextFloat();
			// randomSum += randomSortedArray[i];
		}

		// for (int i = 0; i < _particles.length; i++)
		// {
		// randomSortedArray[i] = randomSortedArray[i] / randomSum;
		// }

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

		histogramViewer.updateHistogram(values);
	}

	void printParticlesInfo()
	{
		PointF mean = calculateParticleMeanPosition(_particles);
		PointF deviation = calculateParticleSDeviationPosition(_particles, mean);
		PointF deviationToRealValue = calculateParticleSDeviationPosition(_particles, _latestPosition.getPosition());

		System.out.println("Actual position: " + _latestPosition.getX() + " - " + _latestPosition.getY());
		System.out.println("Particle mean: " + mean.x + " - " + mean.y);
		System.out.println("Particle std: " + deviation.x + " - " + deviation.y);
		System.out.println("Std to actual position: " + deviationToRealValue.x + " - " + deviationToRealValue.y);
		System.out.println();
	}

	void printBestParticleInfo()
	{
		Particle bestParticle = getBestParticle();

		System.out.println("Actual position: " + _latestPosition.getX() + " - " + _latestPosition.getY());
		System.out.println("Best Particle: " + bestParticle.getPosition().getX() + " - "
				+ bestParticle.getPosition().getY());
		System.out.println("Difference Best Particle: "
				+ Math.abs(bestParticle.getPosition().getX() - _latestPosition.getX()) + " - "
				+ Math.abs(bestParticle.getPosition().getY() - _latestPosition.getY()));
		System.out.println();
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
			mean.x += particle.getPosition().getX();
			mean.y += particle.getPosition().getY();
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
			deviation.x += (particle.getPosition().getX() - mean.x) * (particle.getPosition().getX() - mean.x);
			deviation.y += (particle.getPosition().getY() - mean.y) * (particle.getPosition().getY() - mean.y);
		}

		deviation.x = (float) Math.sqrt(deviation.x / particles.length);
		deviation.y = (float) Math.sqrt(deviation.y / particles.length);

		return deviation;
	}

	public BeamModel getBeamModel()
	{
		return _beamModel;
	}

	public Particle[] getParticles()
	{
		return _particles;
	}
}