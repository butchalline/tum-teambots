import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import teambot.common.data.Position;
import teambot.communication.DebugGridPoint;
import teambot.communication.DebugGridPointStatus;
import teambot.pathplanning.Field;
import teambot.pathplanning.Map;
import teambot.pathplanning.Occupation;
import teambot.slam.ProbabilityMap;
import android.graphics.Point;


public class MapConverter
{
	protected int _cellSize;
	
	public MapConverter(int cellSize)
	{
		_cellSize = cellSize;
	}
	
	
	public DebugGridPoint[] convertMap(Map map)
	{
		Set<Entry<Point,Field>> mapGrid = map.getMapGrid();
		
		DebugGridPoint[] debugPoints = new DebugGridPoint[mapGrid.size()];
		teambot.communication.DebugGridPointStatus status = teambot.communication.DebugGridPointStatus.Invalid;
		Point simCoordinates = new Point(0, 0);
		
		int i = 0;
		for(Entry<Point, Field> field : mapGrid)
		{
			if(map.isValidPathPoint(field.getKey()))
				status = teambot.communication.DebugGridPointStatus.Valid;
			else if(field.getValue().getOccupation() == Occupation.occupied)
				status = teambot.communication.DebugGridPointStatus.Wall; 
			else
				status = DebugGridPointStatus.Invalid;
			
			simCoordinates = convertCoordinatesToSimulator(field.getKey());
			debugPoints[i] = new DebugGridPoint((short)simCoordinates.x, (short)simCoordinates.y, (byte)0, status);
			i++;
		}
		
		return debugPoints;
	}
	
	public DebugGridPoint[] convertSlamMap(ProbabilityMap map, Position botPosition)
	{
		Hashtable<Point, Float> mapGrid = map.getMap();
		
		DebugGridPoint[] debugPoints = new DebugGridPoint[mapGrid.size()];
		teambot.communication.DebugGridPointStatus status = teambot.communication.DebugGridPointStatus.Invalid;
		Point simCoordinates = new Point(0, 0);
		Point botPosition_grid = new Point((int)(botPosition.getX() / _cellSize), (int)(botPosition.getY() / _cellSize));
		
		Point currentPoint;
		int i = 0;
		for(Entry<Point, Float> field : mapGrid.entrySet())
		{
			currentPoint = field.getKey();
			
			if(currentPoint.x > botPosition_grid.x - 1 && currentPoint.x < botPosition_grid.x + 1
					&& currentPoint.y > botPosition_grid.y - 1 && currentPoint.y < botPosition_grid.y + 1)
			{
				status = teambot.communication.DebugGridPointStatus.Wall;
				simCoordinates = convertCoordinatesToSimulator(currentPoint);
				debugPoints[i] = new DebugGridPoint((short)simCoordinates.x, (short)simCoordinates.y, (byte)100, status);
				i++;
				continue;
			}
			
			status = DebugGridPointStatus.Invalid;
			simCoordinates = convertCoordinatesToSimulator(currentPoint);
			debugPoints[i] = new DebugGridPoint((short)simCoordinates.x, (short)simCoordinates.y, (byte)(map.getProbability(currentPoint) * 100), status);
			i++;
		}
		
		return debugPoints;
	}
	
	private Point convertCoordinatesToSimulator(Point point)
	{
		Point simCoordinates = new Point(point);
		int offsetYBecauseOfCoordinateFlip = 1;
		simCoordinates.y = PathPlanningAgentUpdater.offsetY - ((point.y + offsetYBecauseOfCoordinateFlip) * _cellSize);
		simCoordinates.x = point.x * _cellSize;
		return simCoordinates;
	}
	
	public int getCellSize()
	{
		return _cellSize;
	}
}
