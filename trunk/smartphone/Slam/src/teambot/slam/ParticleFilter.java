package teambot.slam;

import java.util.Arrays;

import teambot.common.data.PositionOrientation;
import teambot.common.interfaces.IDistanceListener;
import teambot.common.interfaces.IPositionListener;

public class ParticleFilter implements IPositionListener, IDistanceListener
{
	private static int _maxIterations = 1000;
	private float _twoSigmaSquared = 400f;
	private float _distanceNoiseFactor = 0.2f;
	private float _maxWeight, _totalWeight;
	
	private int _particleAmount = 100;
	
	protected Particle[] _particles;
	private int _iterations;
	
	float _slidingFactor = 0.1f;
	NoiseProvider _noiser;
	ProbabilityMap _map;
	BeamModel _beamModel;
	
	public ParticleFilter(float cellSize_mm,float maxRange_mm, float p0, float pOccupation, float pFree, float varianceX, float varianceY, float varianceAngle)
	{
		_particles = new Particle[_particleAmount];
		
		BeamProbabilities rayProbability = new BeamProbabilities(cellSize_mm, maxRange_mm, p0, pOccupation, pFree);
		BeamModel rayModel = new BeamModel(cellSize_mm, maxRange_mm);
		NoiseProvider noiser = new NoiseProvider(varianceX, varianceY, varianceAngle)
		for(Particle particle: _particles){
			PositionOrientation posOr = new PositionOrientation(0, 0, 0);
			ProbabilityMap probMap = new ProbabilityMap(rayProbability);
			particle = new Particle(posOr,probMap,rayModel, noiser, _slidingFactor)
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
		float totalWeight = 0; 
		// Weights ausrechnen
		for(Particle particle: _particles){
			totalWeight += particle.getAndUpdateWeight(distance_mm);
		}
		// Resamplen
		for(Particle particle: _particles){
			
		}
		// Map Aktualisieren 
		
		
	}
	
	
}
