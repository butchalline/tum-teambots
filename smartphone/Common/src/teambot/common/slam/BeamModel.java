package teambot.common.slam;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.commons.lang3.ArrayUtils;

import teambot.common.data.Pose;
import teambot.common.slam.BeamModel;
import teambot.common.slam.Occupation;
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

	public synchronized LinkedList<SimpleEntry<Point, Occupation>> calculateBeam(float distance_mm, Pose pose)
	{

		PointF rayEnd = new PointF();

		rayEnd.x = (float) (pose.getX() + Math.cos(pose.getAngleInRadian()) * distance_mm);
		rayEnd.y = (float) (pose.getY() + Math.sin(pose.getAngleInRadian()) * distance_mm);

		rayEnd = realToGridCoordinates(rayEnd);
		PointF rayStart = realToGridCoordinates(pose.getPosition());

		LinkedList<SimpleEntry<Point, Occupation>> pointsOnBeam = new LinkedList<SimpleEntry<Point, Occupation>>();

		// if (distance_mm < _maxRange_mm)
		// {
		// Point rayEndDiscretized = new Point();
		//
		// if(position.getAngleInDegree() > 0)
		// rayEndDiscretized.y = (int)rayEnd.y;
		// else
		// rayEndDiscretized.y = (int)Math.ceil(rayEnd.y);
		//
		// if(position.getAngleInDegree() < 90 && position.getAngleInDegree() >
		// -90)
		// rayEndDiscretized.x = (int)rayEnd.x;
		// else
		// rayEndDiscretized.x = (int)Math.ceil(rayEnd.x);
		//
		// pointsOnBeam.add(new SimpleEntry<Point,
		// Occupation>(rayEndDiscretized, Occupation.occupied));
		// }

		for (Object freePoint : calculateFreePoints(distance_mm, rayStart, rayEnd))
		{
			pointsOnBeam.add(new SimpleEntry<Point, Occupation>((Point)freePoint, Occupation.free));
		}

		if(distance_mm < _maxRange_mm)
			pointsOnBeam.getLast().setValue(Occupation.occupied);

		return pointsOnBeam;
	}

	public synchronized LinkedList<SimpleEntry<Point, Occupation>> calculateBeamMaxRange(Pose pose)
	{
		return calculateBeam(_maxRange_mm, pose);
	}

	public PointF realToGridCoordinates(PointF point)
	{
		return new PointF(point.x / _cellSize_mm, point.y / _cellSize_mm);
	}

	public LinkedList<SimpleEntry<Point, Occupation>> realToGridCoordinates(
			LinkedList<SimpleEntry<Point, Occupation>> points)
	{
		LinkedList<SimpleEntry<Point, Occupation>> gridPoints = new LinkedList<SimpleEntry<Point, Occupation>>();

		Point point;

		for (SimpleEntry<Point, Occupation> pointSet : points)
		{
			point = pointSet.getKey();
			gridPoints.add(new SimpleEntry<Point, Occupation>(new Point(point.x / (int) _cellSize_mm, point.y
					/ (int) _cellSize_mm), pointSet.getValue()));
		}

		return gridPoints;
	}

	private Object[] calculateFreePoints(float distance, PointF rayStart, PointF rayEnd)
	{
		float deltaX = rayEnd.x - rayStart.x;
		float deltaY = rayEnd.y - rayStart.y;
		boolean reverseArray = false;

		Vector<Point> freePoints = new Vector<Point>();
		if (Math.abs(deltaX) > Math.abs(deltaY))
		{
			float a = deltaY / deltaX;
			float b = rayStart.y - a * rayStart.x;
			int min_x = (int) Math.floor(Math.min(rayStart.x, rayEnd.x));
			int max_x = (int) Math.floor(Math.max(rayStart.x, rayEnd.x));

			if (rayStart.x > rayEnd.x)
				reverseArray = true;

			for (int x = min_x; x <= max_x; x++)
			{
				int y = (int) Math.floor(a * x + b);

				Point newLeftField = new Point(x - 1, y);
				if (!freePoints.isEmpty() && freePoints.lastElement().y != newLeftField.y)
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
			int min_y = (int) Math.floor(Math.min(rayStart.y, rayEnd.y));
			int max_y = (int) Math.floor(Math.max(rayStart.y, rayEnd.y));

			if (rayStart.y > rayEnd.y)
				reverseArray = true;

			for (int y = min_y; y <= max_y; y++)
			{
				int x = (int) Math.floor(a * y + b);

				Point newBottomField = new Point(x, y - 1);
				if (!freePoints.isEmpty() && freePoints.lastElement().x != newBottomField.x)
				{
					freePoints.add(newBottomField);
				}
				Point newTopField = new Point(x, y);
				freePoints.add(newTopField);
			}
		}

		Object[] freePointsArray = freePoints.toArray();

		if(reverseArray)
			ArrayUtils.reverse(freePointsArray);

		return freePointsArray;
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
