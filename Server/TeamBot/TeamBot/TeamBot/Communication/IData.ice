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

		class TBPosition extends TBFrame
		{
			float distance; //in millimeter or in angle
		};

		class TBInfraredData extends TBFrame
		{
			byte leftDistance;
			byte middleDistance;
			byte rightDistance;
		};

		interface IData
		{
			void receive(TBFrame data);
		};
	};
 };