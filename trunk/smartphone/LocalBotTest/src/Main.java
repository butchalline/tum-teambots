import teambot.common.Bot;
import teambot.common.Settings;
import teambot.common.data.Pose;
import teambot.common.slam.MapConverter;
import teambot.common.utils.Constants;
import teambot.communication.IDataServerPrxHelper;
import teambot.pathplanning.Agent;
import teambot.simulator.SimulatorProxy;
import android.graphics.PointF;


public class Main
{
	public static final String _ip = "127.0.0.1";
	
	public static void main(String[] args)
	{		
		int cellSize_mm = 50;
		Pose pose = new Pose(new PointF(5000, Settings.mapOffsetY - 5625), 90 * Constants.DegreeToRadian);
		
		Bot bot = new Bot(_ip, null);
		Bot.getPose().addToAll(pose);
		IDataServerPrxHelper simulatorProxy = new IDataServerPrxHelper();
		Bot.networkHub().connectToRemoteTcpProxy("Simulator", "127.0.0.1", "55001", simulatorProxy);
		SimulatorProxy simulator = new SimulatorProxy(simulatorProxy);
		Ice.ObjectAdapter objectAdapter = Bot.networkHub().addLocalTcpProxy(simulator, Bot.id() + "_sim", "55000");
		
		try
		{
			simulatorProxy.ice_getConnection().setAdapter(objectAdapter);
			simulatorProxy.addClient(Bot.networkHub().getIdentity());
		}
		catch (Ice.ConnectionRefusedException ex)
		{
			System.out.println("Simulator not running? Restart the program if you want to connect to the simulator");
		}
		
		Agent agent = new Agent(200, 200, cellSize_mm, 3);		
		PathPlanningAgentUpdater agentUpdater = new PathPlanningAgentUpdater(simulator, agent, pose, Bot.getParticleFilter(), new MapConverter(cellSize_mm), 60);
		PathPlanningUpdater pathUpdater = new PathPlanningUpdater(simulator, agent, pose, agentUpdater);
		new Thread(pathUpdater).start();
		bot.setupUsb(agentUpdater);
		bot.run();
	}
}
