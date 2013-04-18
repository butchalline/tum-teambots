package teambot.pathplanning;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import android.graphics.Point;
import android.graphics.PointF;

public class Map implements IPathMapUpdate {
	int _subCellSize;
	int _distanceFromWall_cells;
	Hashtable<Point, Field> _mapGrid = new Hashtable<Point, Field>();
	Hashtable<Point, ValidityBase> _validPathGrid = new Hashtable<Point, ValidityBase>();
	boolean _firstMeasurement = true;

	public Map(int subCellSize, int distanceFromWall_cells) {
		this._subCellSize = subCellSize;
		_distanceFromWall_cells = distanceFromWall_cells;
		int neighbourCountToWall = (distanceFromWall_cells*2 + 1) * (distanceFromWall_cells*2 + 1);
		Valid.setCountOfFreeNeighboursForValidity(neighbourCountToWall);
	}

	public Point[] getFreePathPoints() {
		LinkedList<Point> freePointsList = new LinkedList<Point>();
		Iterator<Entry<Point, ValidityBase>> it = _validPathGrid.entrySet().iterator();
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
		if(_mapGrid.containsKey(point))
		{
			_mapGrid.get(point).setOccupation(occupation);
			return;
		}
		_mapGrid.put(point, new Field(_subCellSize, point, this, occupation));
	}

	public Occupation getOccupation(Point p) {
		if(_mapGrid.containsKey(p))
			return _mapGrid.get(p).getOccupation();

		return Occupation.unknown;
	}

	public boolean isValidPathPoint(Point point) {
		if(_validPathGrid.containsKey(point))
			return _validPathGrid.get(point).isValid();

		return false;
	}

	public synchronized void addMeasurement(PointF start_of_ray, float theta, float distance, float max_range) {

		if(_firstMeasurement)
		{
			Point startPosition = new Point((int) start_of_ray.x, (int) start_of_ray.y);
			Point position;
			for(int x = startPosition.x - _distanceFromWall_cells; x <= startPosition.x + _distanceFromWall_cells; ++x)
			{
				for(int y = startPosition.y - _distanceFromWall_cells; y <= startPosition.y + _distanceFromWall_cells; ++y)
				{
					position = new Point(x, y);
					_mapGrid.put(position, new Field(this._subCellSize, position, this));
					_mapGrid.get(position).setOccupation(Occupation.free);
				}
			}

			_mapGrid.get(start_of_ray);
			_firstMeasurement = false;
		}

		PointF end_of_ray = new PointF();

		end_of_ray.x = (float) (start_of_ray.x + Math.cos(theta) * distance);
		end_of_ray.y = (float) (start_of_ray.y + Math.sin(theta) * distance);

		PointF start_of_ray_sf = new PointF(start_of_ray.x * this._subCellSize, start_of_ray.y * this._subCellSize);
		PointF end_of_ray_sf = new PointF(end_of_ray.x * this._subCellSize, end_of_ray.y * this._subCellSize);

		float delta_x = end_of_ray_sf.x - start_of_ray_sf.x;
		float delta_y = end_of_ray_sf.y - start_of_ray_sf.y;
		Vector<Point> intersectionFields = new Vector<Point>();

		if (Math.abs(delta_x) > Math.abs(delta_y))
		{
			float a = delta_y / delta_x;
			float b = start_of_ray_sf.y - a * start_of_ray_sf.x;
			int min_x = (int) Math.min(start_of_ray_sf.x, end_of_ray_sf.x) + 1;
			int max_x = (int) Math.max(start_of_ray_sf.x, end_of_ray_sf.x);

			for (int x = min_x; x <= max_x; x++)
			{
				int y = (int) (a * x + b);

				Point newLeftField = new Point(x - 1, y);
				if (intersectionFields.isEmpty() || intersectionFields.lastElement().y != newLeftField.y)
				{
					intersectionFields.add(newLeftField);
				}

				Point newRightField = new Point(x, y);
				intersectionFields.add(newRightField);
			}

		}
		else
		{
			float a = delta_x / delta_y;
			float b = start_of_ray_sf.x - a * start_of_ray_sf.y;
			int min_y = (int) Math.min(start_of_ray_sf.y, end_of_ray_sf.y) + 1;
			int max_y = (int) Math.max(start_of_ray_sf.y, end_of_ray_sf.y);

			for (int y = min_y; y <= max_y; y++)
			{
				int x = (int) (a * y + b);

				Point newBottomField = new Point(x, y - 1);
				if (intersectionFields.isEmpty() || intersectionFields.lastElement().x != newBottomField.x)
				{
					intersectionFields.add(newBottomField);
				}
				Point newTopField = new Point(x, y);
				intersectionFields.add(newTopField);
			}
		}

		Point gridPoint = new Point();
		Point subGridPoint = new Point();

		for (int i = 0; i < intersectionFields.size(); i++)
		{
			gridPoint.x = intersectionFields.elementAt(i).x / this._subCellSize;
			gridPoint.y = intersectionFields.elementAt(i).y / this._subCellSize;

			subGridPoint.x = intersectionFields.elementAt(i).x % this._subCellSize;
			subGridPoint.y = intersectionFields.elementAt(i).y % this._subCellSize;

			if(_mapGrid.containsKey(gridPoint))
				_mapGrid.get(gridPoint).setSubFieldFree(subGridPoint.x, subGridPoint.y);
			else
			{
		//		System.out.println("1: Adding Point: " + gridPoint.toString() + "HashValue: " + gridPoint.hashCode());
				_mapGrid.put(new Point(gridPoint), new Field(this._subCellSize, new Point(gridPoint), this));
				_mapGrid.get(gridPoint).setSubFieldFree(subGridPoint.x, subGridPoint.y);
			}
		}

		// If the distance is smaller than the max_range of the sensor than the
		// ray hit an obstacle. In this case set the field at the end of the
		// ray as occupied.
		if (distance < max_range) {
			gridPoint.x = (int) end_of_ray_sf.x / this._subCellSize;
			gridPoint.y = (int) end_of_ray_sf.y / this._subCellSize;

			subGridPoint.x = ((int) end_of_ray_sf.x) % this._subCellSize;
			subGridPoint.y = ((int) end_of_ray_sf.y) % this._subCellSize;

			if(_mapGrid.containsKey(gridPoint))
				_mapGrid.get(gridPoint).setSubFieldOccupied(subGridPoint.x, subGridPoint.y);
			else
			{
			//	System.out.println("2: Adding Point: " + gridPoint.toString() + " HashValue:" + gridPoint.hashCode());
				_mapGrid.put(new Point(gridPoint), new Field(this._subCellSize, new Point(gridPoint), this));
				_mapGrid.get(gridPoint).setSubFieldOccupied(subGridPoint.x, subGridPoint.y);
			}
		}
		//System.out.println("----------------------------------------------------------------------------------------");
	}

	@Override
	public void updatePathMap(Point point, Occupation occupation) {
		if(occupation == Occupation.occupied)
		{
			for(int x = point.x - _distanceFromWall_cells; x <= point.x + _distanceFromWall_cells; ++x)
			{
				for(int y = point.y - _distanceFromWall_cells; y <= point.y + _distanceFromWall_cells; ++y)
				{
					_validPathGrid.put(new Point(x, y), new InValid());
				}
			}
		}
		else
		{
			for(int x = point.x - _distanceFromWall_cells; x <= point.x + _distanceFromWall_cells; ++x)
			{
				for(int y = point.y - _distanceFromWall_cells; y <= point.y + _distanceFromWall_cells; ++y)
				{
					if(this._validPathGrid.containsKey(new Point(x, y)))
					{
						_validPathGrid.get(new Point(x, y)).incCounter();
					}
					else
					{
						Valid valid = new Valid();
						valid.incCounter();
						_validPathGrid.put(new Point(x, y), valid);
					}

				}
			}
		}
	}

	public Set<Point> getMapGrid()
	{
		return _mapGrid.keySet();
	}
}
