package teambot.slam;

import teambot.common.data.PositionOrientation;
import teambot.common.interfaces.IDistanceListener;
import teambot.common.interfaces.IPositionListener;

public class ParticleFilter implements IPositionListener, IDistanceListener
{
	public static int _maxIterations = 1000;
	private float _twoSigmaSquared = 400f;
	private float _distanceNoiseFactor = 0.2f;
	private float _maxWeight, _totalWeight;
	
	protected Particle[] _particles;
	private int _iterations;
	
	float _slidingFactor;
	NoiseProvider _noiser;
	ProbabilityMap _map;
	BeamModel _beamModel;
	
	@Override
	public void callback_PositionChanged(PositionOrientation newPositionOrientation)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callback_NewMeasurement(float distance)
	{
		// TODO Auto-generated method stub
		
	}
	
	
}
