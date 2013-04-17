package teambot.pathplanning;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Vector;

import android.graphics.Point;
import android.graphics.PointF;

public class Map implements IPathMapUpdate {
	int sub_size;
	Hashtable<Point, Field> mapGrid = new Hashtable<Point, Field>();
	Hashtable<Point, ValidityBase> validPathGrid = new Hashtable<Point, ValidityBase>();

	public Map(int sub_size) {
		this.sub_size = sub_size;
	}

	public Point[] getFreePathPoints() {
		LinkedList<Point> freePointsList = new LinkedList<Point>();
		Iterator<Entry<Point, ValidityBase>> it = validPathGrid.entrySet().iterator();
		Entry<Point, ValidityBase> entry;

		while(it.hasNext())
		{
			entry = it.next();
			if(entry.getValue().isValid())
				freePointsList.add(entry.getKey());
		}

		Point[] points = new Point[freePointsList.size()];
		freePointsList.toArray(points);

		return points;
	}

	public void setOccupation(Point point, Occupation occupation)
	{
		if(mapGrid.containsKey(point))
		{
			mapGrid.get(point).setOccupation(occupation);
			return;
		}
		mapGrid.put(point, new Field(sub_size, point, this, occupation));
	}

	public Occupation getOccupation(Point p) {
		if(mapGrid.containsKey(p))
			return mapGrid.get(p).getOccupation();

		return Occupation.unknown;
	}

	public boolean isValidPathPoint(Point p) {
		if(validPathGrid.containsKey(p))
			return validPathGrid.get(p).isValid();

		return false;
	}

	public synchronized void addMeasurement(PointF start_of_ray, float theta, float distance, float max_range) {
		PointF end_of_ray = new PointF();

		end_of_ray.x = (float) (start_of_ray.x + Math.cos(theta) * distance);
		end_of_ray.y = (float) (start_of_ray.y + Math.sin(theta) * distance);

		PointF start_of_ray_sf = new PointF(start_of_ray.x * this.sub_size, start_of_ray.y * this.sub_size);
		PointF end_of_ray_sf = new PointF(end_of_ray.x * this.sub_size, end_of_ray.y * this.sub_size);

		float delta_x = end_of_ray_sf.x - start_of_ray_sf.x;
		float delta_y = end_of_ray_sf.y - start_of_ray_sf.y;
		Vector<Point> intersectionFields = new Vector<Point>();
		if (Math.abs(delta_x) > Math.abs(delta_y)) {
			float a = delta_y / delta_x;
			float b = start_of_ray_sf.y - a * start_of_ray_sf.x;
			int min_x = (int) Math.min(start_of_ray_sf.x, end_of_ray_sf.x) + 1;
			int max_x = (int) Math.max(start_of_ray_sf.x, end_of_ray_sf.x);

			for (int x = min_x; x <= max_x; x++) {
				int y = (int) (a * x + b);

				Point newLeftField = new Point(x - 1, y);
				if (intersectionFields.isEmpty() || intersectionFields.lastElement().x != newLeftField.x
						|| intersectionFields.lastElement().y != newLeftField.y) {
					intersectionFields.add(newLeftField);
				}

				Point newRightField = new Point(x, y);
				intersectionFields.add(newRightField);
			}

		} else {
			float a = delta_x / delta_y;
			float b = start_of_ray_sf.x - a * start_of_ray_sf.y;
			int min_y = (int) Math.min(start_of_ray_sf.y, end_of_ray_sf.y) + 1;
			int max_y = (int) Math.max(start_of_ray_sf.y, end_of_ray_sf.y);

			for (int y = min_y; y <= max_y; y++) {
				int x = (int) (a * y + b);

				Point newBottomField = new Point(x, y - 1);
				if (intersectionFields.isEmpty() || intersectionFields.lastElement().x != newBottomField.x
						|| intersectionFields.lastElement().y != newBottomField.y) {
					intersectionFields.add(newBottomField);
				}
				Point newTopField = new Point(x, y);
				intersectionFields.add(newTopField);
			}
		}

		Point gridPoint = new Point();
		Point subGridPoint = new Point();


	/*	Iterator<Entry<Point, Field>> it = mapGrid.entrySet().iterator();
		Entry<Point, Field> entry;
		int v = 0;
		while(it.hasNext())
		{
			entry = it.next();
			System.out.println("1: Point " + v +" in HashTable: " + entry.getKey().toString() + " HashValue: " + entry.getKey().hashCode());
			v++;
		}*/


		for (int i = 0; i < intersectionFields.size(); i++)
		{
			gridPoint.x = intersectionFields.elementAt(i).x / this.sub_size;
			gridPoint.y = intersectionFields.elementAt(i).y / this.sub_size;

			subGridPoint.x = intersectionFields.elementAt(i).x % this.sub_size;
			subGridPoint.y = intersectionFields.elementAt(i).y % this.sub_size;

			if(mapGrid.containsKey(gridPoint))
				mapGrid.get(gridPoint).setSubFieldFree(subGridPoint.x, subGridPoint.y);
			else
			{
		//		System.out.println("1: Adding Point: " + gridPoint.toString() + "HashValue: " + gridPoint.hashCode());
				mapGrid.put(new Point(gridPoint.x, gridPoint.y), new Field(this.sub_size, gridPoint, this));
				mapGrid.get(gridPoint).setSubFieldFree(subGridPoint.x, subGridPoint.y);
			}
		}


/*		it = mapGrid.entrySet().iterator();
		int q = 0;
		while(it.hasNext())
		{
			entry = it.next();
			System.out.println("2: Point " + q +" in HashTable: " + entry.getKey().toString() + " HashValue: " + entry.getKey().hashCode());
			q++;
		}*/

		// If the distance is smaller than the max_range of the sensor than the
		// ray hit an obstacle. In this case set the field at the end of the
		// ray as occupied.
		if (distance < max_range) {
			gridPoint.x = (int) end_of_ray_sf.x / this.sub_size;
			gridPoint.y = (int) end_of_ray_sf.y / this.sub_size;

			subGridPoint.x = ((int) end_of_ray_sf.x) % this.sub_size;
			subGridPoint.y = ((int) end_of_ray_sf.y) % this.sub_size;

			if(mapGrid.containsKey(gridPoint))
				mapGrid.get(gridPoint).setSubFieldOccupied(subGridPoint.x, subGridPoint.y);
			else
			{
			//	System.out.println("2: Adding Point: " + gridPoint.toString() + " HashValue:" + gridPoint.hashCode());
				mapGrid.put(new Point(gridPoint.x, gridPoint.y), new Field(this.sub_size, gridPoint, this));
				mapGrid.get(gridPoint).setSubFieldOccupied(subGridPoint.x, subGridPoint.y);
			}
		}
		//System.out.println("----------------------------------------------------------------------------------------");
	}

	@Override
	public void updatePathMap(Point point, Occupation occupation) {
		if(occupation == Occupation.occupied)
		{
			for(int x = point.x - 2; x <= point.x + 2; ++x)
			{
				for(int y = point.y - 2; y <= point.y + 2; ++y)
				{
					validPathGrid.put(new Point(x, y), new InValid());
				}
			}
		}
		else
		{
			for(int x = point.x - 2; x <= point.x + 2; ++x)
			{
				for(int y = point.y - 2; y <= point.y + 2; ++y)
				{
					if(this.validPathGrid.containsKey(new Point(x, y)))
					{
						validPathGrid.get(new Point(x, y)).incCounter();
					}
					else
					{
						Valid valid = new Valid();
						valid.incCounter();
						validPathGrid.put(new Point(x, y), valid);
					}

				}
			}
		}


	}
}
