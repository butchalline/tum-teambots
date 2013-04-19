#include <Ice/Identity.ice>

module teambot
{
	module communication
	{

		class TBFrame
		{
			byte Id;
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

		class TBPositionData extends TBFrame
		{
			short x; //in millimeter
			short y; //in millimeter
			short angle; //in angle	in 360.xx * 100
		};

		class TBPositionReached extends TBFrame
		{
			short x; //in millimeter
			short y; //in millimeter
			short angle; //in angle	in 360.xx * 100
		};

		//Debug stuff
		enum DebugGridPointStatus
		{
			Valid, Invalid, Wall
		};
		class DebugGridPoint
		{
			int x; //Grid Position X
			int y; //Grid Position Y
			byte alpha;
			DebugGridPointStatus status;
		};
		sequence<DebugGridPoint> currentMap;

		interface IDataClient
		{
			["amd"] void update(TBFrame data);
		};				  

		interface IDataServer
		{
			["amd"] void update(TBFrame data);
			void addClient(Ice::Identity ident);

			//Debug stuff
			["amd"] void debugMap(currentMap map, short gridWidth);
		};
	};
 };