import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.omg.PortableInterceptor.SUCCESSFUL;

import android.graphics.PointF;

import teambot.common.data.Position;
import teambot.slam.BeamModel;
import teambot.slam.BeamProbabilities;
import teambot.slam.NoiseProvider;
import teambot.slam.Particle;
import teambot.slam.ParticleFilter;
import teambot.slam.ProbabilityMap;
import teambot.visualizer.HistogramViewer;

public class ResampleTest
{

	MockParticleFilter filter;
	static int particleAmount = 100000;
	protected HistogramViewer histogramViewer = new HistogramViewer("Particle Histogram", particleAmount);
	protected HistogramViewer histogramViewerAfter = new HistogramViewer("Particle Histogram", particleAmount);

	@Before
	public void setUp() throws Exception
	{
		filter = new MockParticleFilter(50, 150, 0.5f, 1, 0, new NoiseProvider(0, 0, 0, 0, 0, 0), particleAmount);
	}

	@Test
	public void testProportional()
	{

		Particle[] particles = new Particle[particleAmount];

		float totalWeight = 0;
		for (int i = 0; i < particleAmount; i++)
		{
			particles[i] = new Particle(new Position(new PointF(i + 1, 0), 0), new ProbabilityMap(new BeamProbabilities(
					0, 0, 0)), new BeamModel(50, 150), new NoiseProvider(0, 0, 0, 0, 0, 0), 0, i + 1);
			totalWeight += particles[i].getWeight();
		}

		filter.setParticles(particles);

		double[] values = new double[particles.length];

		for (int i = 0; i < particles.length; ++i)
		{
			values[i] = particles[i].getPosition().getX();
		}

		histogramViewer.updateHistogram(values);
		
		filter.resample(totalWeight);

		particles = filter.getParticles();

		float newTotalWeight = 0;
		for (Particle particle : particles)
		{
			newTotalWeight += particle.getWeight();
//			System.out.println("Pos: "+ particle.getPosition().getX() + "; weight: " + particle.getWeight());
		}
		
		values = new double[particles.length];

		for (int i = 0; i < particles.length; ++i)
			values[i] = particles[i].getPosition().getX();

		histogramViewerAfter.updateHistogram(values);

		
		System.out.println("Diff totalWeights: " + (newTotalWeight - totalWeight) + "; Count of particles: "
				+ particles.length);
		
		try
		{
			Thread.sleep(2 * 1000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testRelativeOccurrence()
	{

		Particle[] particles = new Particle[particleAmount];

		float rand = 0;
		float totalWeight = 0;
		for (int i = 0; i < particleAmount; i++)
		{
			rand = (float) Math.random();
			particles[i] = new Particle(new Position(new PointF(i, rand), 0), new ProbabilityMap(new BeamProbabilities(
					0, 0, 0)), new BeamModel(50, 150), new NoiseProvider(0, 0, 0, 0, 0, 0), 0, rand);
			totalWeight += particles[i].getWeight();
		}

		filter.setParticles(particles);

		filter.resample(totalWeight);

		Particle[] resampledParticles = filter.getParticles();

		float newTotalWeight = 0;
		Particle oldParticle;

		for (Particle particle : resampledParticles)
		{
			newTotalWeight += particle.getWeight();
			oldParticle = particles[(int) particle.getPosition().getX()];
			oldParticle.getPosition().setAngleInRadian(oldParticle.getPosition().getAngleInRadian() + 1);
		}

		double expectedNumber = 0;
		double difference = 0;

		for (Particle particle : particles)
		{
			expectedNumber = (particle.getWeight() / totalWeight) * particleAmount;
			difference += Math.abs(expectedNumber - particle.getPosition().getAngleInRadian());
		}

		System.out.println("Diff totalWeights: " + (newTotalWeight - totalWeight) + "; Count of particles: "
				+ particles.length);
		System.out.println("Mean of deviation of expected number of occurrence: " + (difference / particleAmount));
	}
}
