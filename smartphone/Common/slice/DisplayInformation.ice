#include <Ice/Identity.ice>

module teambot
{
	sequence<string> StringArray;
	class DisplayInformation
	{
		float leftWheelRefVelocity;
		float rightWheelRefVelocity;
		StringArray idsOfKnownBots;
	};
};