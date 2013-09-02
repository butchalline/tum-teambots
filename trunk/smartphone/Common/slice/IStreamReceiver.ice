module teambot
{
	module remote
	{
		sequence<byte> ByteArray;
		sequence<short> ShortArray;
		
		class BitmapSlice
		{
			int width;
			int height;
			ByteArray data;
		};
		
		["amd"] interface IStreamReceiver
		{
			void bitmapCallback(BitmapSlice newBitmap);
			void audioCallback(ByteArray newAudioBytes);
		};
	};
};