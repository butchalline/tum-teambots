package teambot.test;
import teambot.slam.NoiseProvider;
import teambot.slam.Particle;
import teambot.slam.ParticleFilter;


public class MockParticleFilter extends ParticleFilter
{

	public MockParticleFilter(float cellSize_mm, float maxRange_mm, float p0, float pOccupation, float pFree,
			NoiseProvider noiser, int particleAmount)
	{
		super(cellSize_mm, maxRange_mm, p0, pOccupation, pFree, noiser, particleAmount);
	}
	
	public void setParticles(Particle[] particles)
	{
		_particles = particles;
	}
	
	public void resample(float totalWeight)
	{		
		super.resample(totalWeight);
	}
}
