module teambot
{
	module communication
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
			short distance; //in millimeter or in angle	in 360.xx * 100
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