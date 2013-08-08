module teambot
{
	module common
	{
		sequence<byte> Packet;
		interface ITeambot {
			string getIdRemote();
			void setVelocity(Packet velocityPacket);
		};
	};
};