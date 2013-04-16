package teambot.slam;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.Vector;

import teambot.common.data.PositionOrientation;
import teambot.pathplanning.Occupation;
import android.graphics.Point;
import android.graphics.PointF;

public class BeamModel {
	
	protected float _gridSize_mm;
	protected float _maxRange_mm;

	public BeamModel(float cellSize_mm, float maxRange_mm) {
		// TODO Auto-generated constructor stub
	}

	public synchronized LinkedList<SimpleEntry<Point, Occupation>> calculateBeam(float distance_mm, PositionOrientation positionOrientation) {
		
		PointF rayEnd = new PointF(); 
		
		rayEnd.x = (float) (positionOrientation.getX() + Math.cos(positionOrientation.getAngleInRadian()) * distance_mm);
		rayEnd.y = (float) (positionOrientation.getY() + Math.sin(positionOrientation.getAngleInDegree()) * distance_mm);
		
		rayEnd = realToGridCoordinates(rayEnd);
		PointF rayStart = realToGridCoordinates(positionOrientation.getPosition());
		
		LinkedList<SimpleEntry<Point, Occupation>> pointsOnBeam = new LinkedList<SimpleEntry<Point,Occupation>>();		
		
		if (distance_mm < _maxRange_mm)
		{
			Point rayEndDiscretized = new Point((int)(rayEnd.x / _gridSize_mm), (int)(rayEnd.y / _gridSize_mm));
			pointsOnBeam.add(new SimpleEntry<Point, Occupation>(rayEndDiscretized, Occupation.occupied));
		}		
		
		for(Point freePoint : calculateFreePoints(distance_mm, rayStart, rayEnd))
		{
			pointsOnBeam.add(new SimpleEntry<Point, Occupation>(freePoint, Occupation.free));
		}
		
		return pointsOnBeam;
	}
	
	public PointF realToGridCoordinates(PointF point)
	{
		return new PointF(point.x / _gridSize_mm, point.y / _gridSize_mm);
	}
	
	private Vector<Point> calculateFreePoints(float distance, PointF rayStart, PointF rayEnd)
	{
		float delta_x = rayEnd.x - rayStart.x;
		float delta_y = rayEnd.y - rayStart.y;
		
		Vector<Point> freePoints = new Vector<Point>();
		if (Math.abs(delta_x) > Math.abs(delta_y)) {
			float a = delta_y / delta_x;
			float b = rayStart.y - a * rayStart.x;
			int min_x = (int) Math.min(rayStart.x, rayEnd.x) + 1;
			int max_x = (int) Math.max(rayStart.x, rayEnd.x);

			for (int x = min_x; x <= max_x; x++) {
				int y = (int) (a * x + b);

				Point newLeftField = new Point(x - 1, y);
				if (freePoints.isEmpty() || freePoints.lastElement().x != newLeftField.x
						|| freePoints.lastElement().y != newLeftField.y) {
					freePoints.add(newLeftField);
				}

				Point newRightField = new Point(x, y);
				freePoints.add(newRightField);
			}

		} else {
			float a = delta_x / delta_y;
			float b = rayStart.x - a * rayStart.y;
			int min_y = (int) Math.min(rayStart.y, rayEnd.y) + 1;
			int max_y = (int) Math.max(rayStart.y, rayEnd.y);

			for (int y = min_y; y <= max_y; y++) {
				int x = (int) (a * y + b);

				Point newBottomField = new Point(x, y - 1);
				if (freePoints.isEmpty() || freePoints.lastElement().x != newBottomField.x
						|| freePoints.lastElement().y != newBottomField.y) {
					freePoints.add(newBottomField);
				}
				Point newTopField = new Point(x, y);
				freePoints.add(newTopField);
			}
		}
		return freePoints;
	}
}
