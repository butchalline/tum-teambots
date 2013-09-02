#include <Ice/Identity.ice>

module teambot
{
	enum RegisterClass
	{
		SERVER, INFORMATIONDISPLAYER, STREAMRECEIVER
	};

	module common
	{
		sequence<byte> Packet;
		interface ITeambot {
			string getIdRemote();
			void setVelocity(Packet velocityPacket);
			void addClient(Ice::Identity ident, RegisterClass clientClass);
		};
	};
};