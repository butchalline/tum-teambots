package teambot.remote;

import java.awt.Color;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import teambot.common.Bot;
import teambot.common.Settings;
import teambot.common.communication.BotNetworkLookUp;
import teambot.common.communication.NetworkHub;
import teambot.common.utils.ThreadUtil;

public class RemotePC
{
	public static final String ip = "192.168.178.51";
	
	public static void main(String[] args)
	{	
		
		Bot._botId = ip;
		JFrame frame = new JFrame("Tembot Video Stream");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	    JPanel panel = new JPanel(); 
	    panel.setSize(500,640);
	    panel.setBackground(Color.CYAN);
	    
	    JLabel label = new JLabel(); 
	    panel.add(label);
	    frame.getContentPane().add(panel); 
	    frame.setSize(640, 480);
	    frame.setVisible(true);
	    
		AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
		SourceDataLine line;
		
		try
		{
			line = AudioSystem.getSourceDataLine(format);
		} catch (LineUnavailableException e)
		{
			e.printStackTrace();
			return;
		};
	    		
		
        try
		{
			line.open(format, 4096 * Settings.soundBufferSizeMultiplier);
		} catch (LineUnavailableException e)
		{
			e.printStackTrace();
			return;
		}
        
        line.start();
	    
		StreamReceiver streamReceiver = new StreamReceiver(label, line);
		
		BotKeeper botkeeper = new BotKeeper();
		NetworkHub networkHub = new NetworkHub(botkeeper, ip);
		networkHub.start();
		Ice.ObjectAdapter streamObjectAdapter = networkHub.addLocalUdpProxy(streamReceiver, "StreamReceiver", Settings.streamingPort);
		botkeeper.addNetworkHub(networkHub);
		botkeeper.addStreamObjectAdapter(streamObjectAdapter);
		@SuppressWarnings("unused")
		BotNetworkLookUp botlookUp = new BotNetworkLookUp(networkHub, botkeeper);
        
		while(true)
		{
			ThreadUtil.sleepMSecs(1000);			
		}
		
//		botlookUp.stop();
//		networkHub.stop();
//		soundThread.stop();
	}
}
