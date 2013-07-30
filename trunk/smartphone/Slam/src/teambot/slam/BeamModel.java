package teambot.slam;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.Vector;

import teambot.common.data.Position;
import teambot.pathplanning.Occupation;
import android.graphics.Point;
import android.graphics.PointF;

public class BeamModel
{

	protected float _cellSize_mm;
	protected float _maxRange_mm;

	public BeamModel(float cellSize_mm, float maxRange_mm)
	{
		_cellSize_mm = cellSize_mm;
		_maxRange_mm = maxRange_mm;
	}

	public BeamModel(BeamModel model)
	{
		_cellSize_mm = model._cellSize_mm;
		_maxRange_mm = model._maxRange_mm;
	}

	public synchronized LinkedList<SimpleEntry<Point, Occupation>> calculateBeam(float distance_mm, Position position)
	{

		PointF rayEnd = new PointF();

		rayEnd.x = (float) (position.getX() + Math.cos(position.getAngleInRadian()) * distance_mm);
		rayEnd.y = (float) (position.getY() + Math.sin(position.getAngleInRadian()) * distance_mm);
		
		rayEnd = realToGridCoordinates(rayEnd);
		PointF	rayStart = realToGridCoordinates(position.getPosition());

		LinkedList<SimpleEntry<Point, Occupation>> pointsOnBeam = new LinkedList<SimpleEntry<Point, Occupation>>();

		if (distance_mm < _maxRange_mm)
		{
			Point rayEndDiscretized = new Point((int) (rayEnd.x), (int) (rayEnd.y));
			pointsOnBeam.add(new SimpleEntry<Point, Occupation>(rayEndDiscretized, Occupation.occupied));
		}

		for (Point freePoint : calculateFreePoints(distance_mm, rayStart, rayEnd))
		{
			pointsOnBeam.add(new SimpleEntry<Point, Occupation>(freePoint, Occupation.free));
		}

		return pointsOnBeam;
	}

	public synchronized LinkedList<SimpleEntry<Point, Occupation>> calculateBeamMaxRange(Position position)
	{
		return calculateBeam(_maxRange_mm, position);
	}

	public PointF realToGridCoordinates(PointF point)
	{
		return new PointF(point.x / _cellSize_mm, point.y / _cellSize_mm);
	}
	
	public LinkedList<SimpleEntry<Point, Occupation>> realToGridCoordinates(LinkedList<SimpleEntry<Point, Occupation>> points)
	{
		LinkedList<SimpleEntry<Point, Occupation>> gridPoints = new LinkedList<SimpleEntry<Point, Occupation>>();
		
		Point point;
		
		for (SimpleEntry<Point, Occupation> pointSet : points)
		{
			point = pointSet.getKey();
			gridPoints.add(new SimpleEntry<Point, Occupation>(new Point(point.x / (int)_cellSize_mm, point.y / (int)_cellSize_mm), pointSet.getValue()));
		}
		
		return gridPoints;
	}

	private Vector<Point> calculateFreePoints(float distance, PointF rayStart, PointF rayEnd)
	{
		float deltaX = rayEnd.x - rayStart.x;
		float deltaY = rayEnd.y - rayStart.y;

		Vector<Point> freePoints = new Vector<Point>();
		if (Math.abs(deltaX) > Math.abs(deltaY))
		{
			float a = deltaY / deltaX;
			float b = rayStart.y - a * rayStart.x;
			int min_x = (int) Math.min(rayStart.x, rayEnd.x) + 1;
			int max_x = (int) Math.max(rayStart.x, rayEnd.x) - 1;

			for (int x = min_x; x <= max_x; x++)
			{
				int y = (int) (a * x + b);

				Point newLeftField = new Point(x - 1, y);
				if (freePoints.isEmpty() || freePoints.lastElement().y != newLeftField.y)
				{
					freePoints.add(newLeftField);
				}

				Point newRightField = new Point(x, y);
				freePoints.add(newRightField);
			}

		} else
		{
			float a = deltaX / deltaY;
			float b = rayStart.x - a * rayStart.y;
			int min_y = (int) Math.min(rayStart.y, rayEnd.y) + 1;
			int max_y = (int) Math.max(rayStart.y, rayEnd.y) - 1;

			for (int y = min_y; y <= max_y; y++)
			{
				int x = (int) (a * y + b);

				Point newBottomField = new Point(x, y - 1);
				if (freePoints.isEmpty() || freePoints.lastElement().x != newBottomField.x)
				{
					freePoints.add(newBottomField);
				}
				Point newTopField = new Point(x, y);
				freePoints.add(newTopField);
			}
		}

		return freePoints;
	}

	public float getCellSize()
	{
		return _cellSize_mm;
	}

	public float getMaxRange()
	{
		return _maxRange_mm;
	}
}
