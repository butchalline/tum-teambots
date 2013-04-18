package teambot.pathplanning;

public class Valid implements ValidityBase {
	private byte counter;
	static int validityCheckFreeNeighbourFieldsCount = 24;
	public Valid()
	{
		counter = 0;
	}

	@Override
	public void incCounter()
	{
		if(!isValid())
		{
			++counter;
		}
	}

	@Override
	public boolean isValid()
	{
		if(counter == validityCheckFreeNeighbourFieldsCount)
			return true;
		return false;
	}

	public static void setCountOfFreeNeighboursForValidity(int count)
	{
		validityCheckFreeNeighbourFieldsCount = count;
	}
}
