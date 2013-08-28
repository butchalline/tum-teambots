package teambot.remote;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IpHelper
{
	/**
	 * Get IP address from first non-localhost interface
	 * 
	 * @return address or empty string
	 */
	public static String getIPAddress()
	{
		try
		{
			for (Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces
					.hasMoreElements();)
			{
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr
						.hasMoreElements();)
				{
					InetAddress ip = enumIpAddr.nextElement();
					if (!ip.isLoopbackAddress() && ip instanceof Inet4Address)
					{
						return ip.getHostAddress().toString();
					}
				}
			}
		} catch (Exception ex)
		{

		}
		return "";
	}
}
