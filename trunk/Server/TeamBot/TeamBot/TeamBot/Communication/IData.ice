module TeamBot
{
	module Communication
	{

		class TBFrame
		{
			byte Id; //defined above
			byte SubId;
			short TimeStamp; //in 10ms
		};

		class TBVelocity extends TBFrame
		{
			byte speedLeft;
			byte speedRight;
		};

		class TBInfraredData extends TBFrame
		{
			byte leftSpeed;
			byte middletSpeed;
			byte rightSpeed;
		};

		interface IData
		{
			void receive(TBFrame vel);
		};
	};
 };