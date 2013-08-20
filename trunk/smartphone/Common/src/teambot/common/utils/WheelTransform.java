package teambot.common.utils;

public class WheelTransform
{
	static float linearSlope = 0;
	static int maxBitValue = 1023;
	static State state = State.LINEAR;
	static float pose = 0;
	static long oldTime = -1;

	static float borderLinearHigh = 323 / 180 * Constants.piAsFloat;
	static float borderHighMiddle = 341.5f / 180 * Constants.piAsFloat;
	static float borderMiddleLow = 350 / 180 * Constants.piAsFloat;
	static float borderLowLinear = 0;

	/**
	 * 
	 * Transformation is non-linear and has one linear part and three plateaus
	 * 
	 */
	enum State
	{
		LINEAR, HIGH, MIDDLE, LOW
	}

	public static float wheelStepsToRadian(int adConverterValue, float refAngleVelocity,
			boolean positiveProportional)
	{
		long newTime = System.currentTimeMillis();
		float dTime = (newTime - oldTime) / 1000;
		oldTime = newTime;
		
		if (adConverterValue < maxBitValue && adConverterValue > 0 && state != State.MIDDLE)
		{
			state = State.LINEAR;
			pose = adConverterValue * linearSlope;
			return pose;
		}

		if (adConverterValue == maxBitValue)
		{
			if (state != State.HIGH)
			{
				state = State.HIGH;
				if (positiveProportional)
				{
					pose = borderLinearHigh;
					return pose;
				} else
				{
					pose = borderHighMiddle;
					return pose;
				}
			}
			pose = pose + dTime * refAngleVelocity;
			return pose;
		}

		if (adConverterValue > 0.4 * maxBitValue && adConverterValue < 0.6 * maxBitValue && state != State.LINEAR)
		{
			if (state != State.MIDDLE)
			{
				state = State.MIDDLE;
				if (positiveProportional)
				{
					pose = borderHighMiddle;
					return pose;
				} else
				{
					pose = borderMiddleLow;
					return pose;
				}
			}
			pose = pose + dTime * refAngleVelocity;
			return pose;
		}
		if (adConverterValue == 0)
		{
			if (state != State.LOW)
			{
				state = State.LOW;

				if (positiveProportional)
					pose = borderMiddleLow;
				else
					pose = borderLowLinear;

				return pose;
			}
			pose = pose + dTime * refAngleVelocity;
			return pose;
		}
		throw new UnsupportedOperationException();
	}
}
