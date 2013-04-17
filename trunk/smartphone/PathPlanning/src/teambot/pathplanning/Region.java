package teambot.pathplanning;


public class Region {
	public int num_fields;
	public int min_x;
	public int max_x;
	public int min_y;
	public int max_y;
	
	public Region(int num_fields, int min_x, int max_x, int min_y, int max_y)
	{
		this.num_fields = num_fields;
		this.min_x = min_x;
		this.max_x = max_x;
		this.min_y = min_y;
		this.max_y = max_y;
	}
}
