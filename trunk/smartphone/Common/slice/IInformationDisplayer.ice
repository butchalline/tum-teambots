module teambot
{
	sequence<string> StringArray;
	class DisplayInformation
	{
		float leftWheelRefVelocity;
		float rightWheelRefVelocity;
		StringArray idsOfKnownBots;
	};

	interface IInformationDisplayer
	{
		["amd"] void infoCallback(DisplayInformation newDisplayInformation);
	};
};