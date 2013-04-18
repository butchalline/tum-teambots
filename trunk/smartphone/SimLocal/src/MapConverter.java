import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import teambot.communication.DebugGridPoint;
import teambot.communication.DebugGridPointStatus;
import teambot.pathplanning.Field;
import teambot.pathplanning.Map;
import teambot.pathplanning.Occupation;
import teambot.pathplanning.ValidityBase;
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
		Hashtable<Point, ValidityBase> validMap = map.getValidPathMapGrid();
		
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
			debugPoints[i] = new DebugGridPoint((short)simCoordinates.x, (short)simCoordinates.y, status);
			i++;
		}
		
		return debugPoints;
	}
	
	private Point convertCoordinatesToSimulator(Point point)
	{
		Point simCoordinates = new Point(point);
		simCoordinates.y = PathPlanningAgentUpdater.offsetY - (point.y * _cellSize);
		simCoordinates.x = point.x * _cellSize;
		return simCoordinates;
	}
}
