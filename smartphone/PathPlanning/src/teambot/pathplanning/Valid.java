package teambot.pathplanning;

public class Valid implements ValidityBase {
	private byte counter;
	static final int validityCheckFreeNeighbourFieldsCount = 24;
	public Valid()
	{
		counter = 0;
	}

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



}
