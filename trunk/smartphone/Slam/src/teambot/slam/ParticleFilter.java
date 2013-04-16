package teambot.slam;

import teambot.common.data.PositionOrientation;
import teambot.common.interfaces.IDistanceListener;
import teambot.common.interfaces.IPositionListener;
import java.util.Random;

public class ParticleFilter implements IPositionListener, IDistanceListener
{
	private static int _maxIterations = 1000;
	private float _twoSigmaSquared = 400f;
	private float _distanceNoiseFactor = 0.2f;
	private float _maxWeight, _totalWeight;
	
	private Random random = new Random();
	private int _particleAmount = 100;
	
	protected Particle[] _particles;
	private int _iterations;
	
	float _slidingFactor = 0.1f;
	NoiseProvider _noiser;
	ProbabilityMap _map;
	BeamModel _beamModel;
	
	public ParticleFilter(float cellSize_mm, float maxRange_mm, float p0, float pOccupation, float pFree, float varianceX, float varianceY, float varianceAngle)
	{
		_particles = new Particle[_particleAmount];
		
		BeamProbabilities rayProbability = new BeamProbabilities(cellSize_mm, maxRange_mm, p0, pOccupation, pFree);
		BeamModel rayModel = new BeamModel(cellSize_mm, maxRange_mm);
		NoiseProvider noiser = new NoiseProvider(varianceX, varianceY, varianceAngle);
		
		for(int i = 0; i < _particles.length; ++i)
		{
			PositionOrientation posOr = new PositionOrientation(0, 0, 0);
			ProbabilityMap probMap = new ProbabilityMap(rayProbability);
			_particles[i] = new Particle(posOr,probMap,rayModel, noiser, _slidingFactor);
		}
	}
	
	@Override
	public void callback_PositionChanged(PositionOrientation newPositionOrientation)
	{
		for(Particle particle: _particles){
			particle.updatePositionOrientation(newPositionOrientation);
		}
		
	}

	@Override
	public void callback_NewMeasurement(float distance_mm)
	{
		
		float[] weightArray = new float[_particles.length];
		float totalWeight = 0; 
		// Weights ausrechnen

		for(Particle particle: _particles){
			totalWeight += particle.updateAndGetWeight(distance_mm);
		}
		
		// Weights sortieren (quasi Dartscheibe erstellen)
		weightArray[0] = 0;
		for(int i = 1; i < _particles.length; i++){
			weightArray[i] = weightArray[i-1] + _particles[i].getWeight()/totalWeight ; 
		}
		
		// Zufallszahlen erstellen (maybe not perfect working) 
		float[] randomSortedArray = new float[_particles.length];
		for(int i = 0; i < _particles.length; i++){
			randomSortedArray[i] = (i + random.nextFloat()) / _particles.length;
		}
		
		// Resamplen
		
		Particle[] newParticles = new Particle[_particles.length];
		Particle actualParticle = _particles[0];
		int newParticleIndex = 0;
		int actualParticleIndex = 0;
		while(newParticleIndex != _particles.length){
			if(randomSortedArray[newParticleIndex] < weightArray[actualParticleIndex]){
				newParticles[newParticleIndex] = new Particle(actualParticle);
				newParticleIndex += 1;
			}
			else if (randomSortedArray[newParticleIndex] >= weightArray[actualParticleIndex]) {
				actualParticleIndex += 1;
				actualParticle = _particles[actualParticleIndex];
			}
		}
		_particles = newParticles;
		// Map Aktualisieren 
		for(Particle particle: _particles){
			particle.updateMap(distance_mm);
		}
	}
	
	
}
