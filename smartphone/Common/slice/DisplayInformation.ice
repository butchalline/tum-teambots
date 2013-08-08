#include <Ice/Identity.ice>

module teambot
{
	sequence<string> StringArray;
	class DisplayInformation
	{
		StringArray idsOfKnownBots;
	};
};