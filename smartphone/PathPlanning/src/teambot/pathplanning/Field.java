package teambot.pathplanning;

import android.graphics.Point;

public class Field extends Point {
	final int sub_size;
	int free_counter;
	int occupied_counter;
	private float occupiedSubGrid[][];
	private Occupation occupation;
	private final IPathMapUpdate mapUpdateCallBack;

	public Field(int sub_size, Point position, IPathMapUpdate mapUpdateCallBack) {
		this.sub_size = sub_size;
		this.x = position.x;
		this.y = position.y;
		this.occupiedSubGrid = null;
		this.free_counter = this.occupied_counter = 0;
		this.occupation = Occupation.unknown;
		this.mapUpdateCallBack = mapUpdateCallBack;
	}

	public Field(int sub_size, Point position, IPathMapUpdate mapUpdateCallBack, Occupation occupation) {
		this.sub_size = sub_size;
		this.x = position.x;
		this.y = position.y;
		this.occupiedSubGrid = null;
		this.free_counter = this.occupied_counter = 0;
		this.occupation = occupation;
		this.mapUpdateCallBack = mapUpdateCallBack;
	}

	public void setSubFieldFree(int x, int y)
	{
		if (this.occupation == Occupation.free)
			return;

		if (this.occupation == Occupation.occupied)
			return;

		if (this.occupiedSubGrid == null)
		{
			this.occupiedSubGrid = new float[sub_size][sub_size];
			for (int i = 0; i < sub_size; i++)
			{
				for (int j = 0; j < sub_size; j++)
				{
					this.occupiedSubGrid[i][j] = 0.5f;
				}
			}
		}

		if (this.occupiedSubGrid[x][y] > 0)
		{
			if (this.occupiedSubGrid[x][y] == 1)
			{
				this.occupied_counter--;
			}
			this.free_counter++;
			this.occupiedSubGrid[x][y] = 0;

			this.updateOccupationStatus();
		}
	}

	private void updateOccupationStatus() {
		if (this.occupied_counter >= 0.1* this.sub_size* this.sub_size)
		{
//			System.out.println("Discovered occupied field at (" + this.x + "," + this.y + ")");
			this.occupation = Occupation.occupied;
			this.occupiedSubGrid = null;
			mapUpdateCallBack.updatePathMap(this, occupation);
			return;
		}
		if (this.free_counter > 0.9* this.sub_size* this.sub_size)
		{
//			System.out.println("Discovered free field at (" + this.x + "," + this.y + ")");
			this.occupation = Occupation.free;
			this.occupiedSubGrid = null;
			mapUpdateCallBack.updatePathMap(this, occupation);
			return;
		}
	}

	public void setSubFieldOccupied(int x, int y)
	{
		if (this.occupation == Occupation.free)
			return;

		if (this.occupation == Occupation.occupied)
			return;

		if (this.occupiedSubGrid == null)
		{
			this.occupiedSubGrid = new float[sub_size][sub_size];
			for (int i = 0; i < sub_size; i++)
			{
				for (int j = 0; j < sub_size; j++)
				{
					this.occupiedSubGrid[i][j] = 0.5f;
				}
			}
		}


		if (this.occupiedSubGrid[x][y] < 1)
		{
			if (this.occupiedSubGrid[x][y] == 0)
			{
				this.free_counter--;
			}
			this.occupied_counter++;
			this.occupiedSubGrid[x][y] = 1;

			this.updateOccupationStatus();
		}
	}

	public Occupation getOccupation() {

		return this.occupation;
	}

	public void setOccupation(Occupation occupation) {

		this.occupation = occupation;
	}
}
