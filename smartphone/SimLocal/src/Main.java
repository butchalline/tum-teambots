import teambot.common.utils.ThreadUtil;
import teambot.simulator.SimulatorProxy;
import teambot.slam.NoiseProvider;
import teambot.slam.ParticleFilter;


public class Main {

	public static void main(String[] args) {
		
		SimulatorProxy simulator = new SimulatorProxy();
		ThreadUtil.sleepSecs(1);
        simulator.start("localhost", "55000", "55001");
        ThreadUtil.sleepSecs(1);
        NoiseProvider noiser = new NoiseProvider(0, 0, 0, 0.05f, 0.05f, 0.05f);
//        NoiseProvider noiser = new NoiseProvider(0, 0, 0, 0, 0, 0);
        ParticleFilter filter = new ParticleFilter(50, 1500, 0.5f, 0.8f, 0.2f, noiser);
        PathPlanningUpdater pathUpdater = new PathPlanningUpdater(simulator, filter, 60);
        ThreadUtil.sleepSecs(1);
        new Thread(pathUpdater).start();
        while(true) {
        	ThreadUtil.sleepSecs(1);
        }
	}
}
