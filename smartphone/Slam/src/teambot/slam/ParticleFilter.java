package teambot.slam;

import teambot.common.data.Position;
import teambot.common.interfaces.IDistanceListener;
import teambot.common.interfaces.IPositionListener;
import java.util.Random;

import android.graphics.PointF;

public class ParticleFilter implements IPositionListener, IDistanceListener
{	
	private Random random = new Random();
	private int _particleAmount = 100;
	private float nThresh = _particleAmount/2;
	
	protected Particle[] _particles;
	protected Position _latestPosition;
	
	float _slidingFactor = 0.1f;
	NoiseProvider _noiser;
	ProbabilityMap _map;
	BeamModel _beamModel;
	
	public ParticleFilter(float cellSize_mm, float maxRange_mm, float p0, float pOccupation, float pFree, float varianceX, float varianceY, float varianceAngle)
	{
		_particles = new Particle[_particleAmount];
		
		BeamProbabilities rayProbability = new BeamProbabilities(p0, pOccupation, pFree);
		BeamModel rayModel = new BeamModel(cellSize_mm, maxRange_mm);
		NoiseProvider noiser = new NoiseProvider(varianceX, varianceY, varianceAngle);
		
		for(int i = 0; i < _particles.length; ++i)
		{
			Position posOr = new Position(0, 0, 0);
			ProbabilityMap probMap = new ProbabilityMap(rayProbability);
			_particles[i] = new Particle(posOr, probMap, rayModel, noiser, _slidingFactor);
		}
	}
	
	@Override
	public void callback_PositionChanged(Position newPosition)
	{
		_latestPosition = newPosition;
		
//		//info prints
//		System.out.println("----------------------before noise added----------------------");
//		printParticlesInfo();
//		printBestParticleInfo();
		
		for(Particle particle: _particles){
			particle.updatePosition(newPosition);
		}
		
//		//info prints
//		System.out.println("----------------------after noise added----------------------");
//		printParticlesInfo();
//		printBestParticleInfo();
	}

	@Override
	public void callback_NewMeasurement(float distance_mm)
	{
		
		
		float totalWeight = 0; 
		// Weights ausrechnen

		for(Particle particle: _particles){
			totalWeight += particle.updateAndGetWeight(distance_mm);
		}
		
		//Abfrage ob Resamplen n�tig
		float invNeff = 0;
		for(Particle particle: _particles){
			invNeff += (particle.getWeight()/totalWeight)*(particle.getWeight()/totalWeight);
		}
		if(1/invNeff< nThresh){
			this.resample(totalWeight);
		}
		
		
		//info prints
//		System.out.println("----------------------after weight update----------------------");
////		printParticlesInfo();
//		printBestParticleInfo();
		
		
		// Map Aktualisieren 
		for(Particle particle: _particles){
			particle.updateMap(distance_mm);
		}
	}
	
	private void resample(float totalWeight) {
		// Resamplen
		// Weights sortieren (quasi Dartscheibe erstellen)
		float[] weightArray = new float[_particles.length];
		weightArray[0] = _particles[0].getWeight()/totalWeight;
		for(int i = 1; i < _particles.length; i++){
			weightArray[i] = weightArray[i-1] + _particles[i].getWeight()/totalWeight ; 
		}
		weightArray[weightArray.length -1] = 1;
		
		// Zufallszahlen erstellen (maybe not perfectly working) 
		float[] randomSortedArray = new float[_particles.length];
		for(int i = 0; i < _particles.length; i++){
			randomSortedArray[i] = (i  + random.nextFloat()) / _particles.length;
		}
		
		// Resamplen
		
		Particle[] newParticles = new Particle[_particles.length];
		Particle actualParticle = _particles[0];
		actualParticle.setWeigth(1.0f/_particleAmount);
		int newParticleIndex = 0;
		int actualParticleIndex = 0;
		
		while(newParticleIndex != _particles.length){
			if(randomSortedArray[newParticleIndex] <= weightArray[actualParticleIndex]){
				newParticles[newParticleIndex] = new Particle(actualParticle);
				newParticleIndex += 1;
			}
			else if (randomSortedArray[newParticleIndex] > weightArray[actualParticleIndex]) {
				actualParticleIndex += 1;
				actualParticle = _particles[actualParticleIndex];
				actualParticle.setWeigth(1.0f/_particleAmount);
			}
		}
		
		
		//Set Weight
		
		
		_particles = newParticles;
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
		
		System.out.println("Actual position: " + _latestPosition.getX() + " - " 
				+ _latestPosition.getY());
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
		for(Particle particle : _particles)
		{
			if(particle.getWeight() > bestParticle.getWeight())
				bestParticle = particle;
		}
		return bestParticle;
	}
	
	PointF calculateParticleMeanPosition(Particle[] particles)
	{
		PointF mean = new PointF(0, 0);
		
		for(Particle particle : particles)
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
		
		for(Particle particle : particles)
		{
			deviation.x += (particle.getPosition().getX() - mean.x)
					* (particle.getPosition().getX() - mean.x);
			deviation.y += (particle.getPosition().getY() - mean.y)
					* (particle.getPosition().getY() - mean.y);
		}
		
		deviation.x = (float) Math.sqrt(deviation.x / particles.length);
		deviation.y = (float) Math.sqrt(deviation.y / particles.length);
		
		return deviation;
	}
}
