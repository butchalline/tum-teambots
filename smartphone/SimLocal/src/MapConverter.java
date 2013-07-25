import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import teambot.common.data.Position;
import teambot.communication.DebugGridPoint;
import teambot.communication.DebugGridPointStatus;
import teambot.pathplanning.Field;
import teambot.pathplanning.Map;
import teambot.pathplanning.Occupation;
import teambot.slam.BeamProbabilities;
import teambot.slam.Particle;
import teambot.slam.ParticleFilter;
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
	
	public DebugGridPoint[] particlesToMap(Particle[] particles)
	{
		DebugGridPoint[] debugPoints = new DebugGridPoint[particles.length*2];
		Point simCoordinates = new Point(0, 0);
		
		Point currentPoint;
		Point orientationOffset;
		int i = 0;
		for(Particle particle : particles)
		{
			currentPoint = new Point((int)(particle.getPosition().getX() / _cellSize), (int)(particle.getPosition().getY() / _cellSize));
			simCoordinates = convertCoordinatesToSimulator(currentPoint);
			debugPoints[i] = new DebugGridPoint((short)simCoordinates.x, (short)simCoordinates.y, (byte)100, teambot.communication.DebugGridPointStatus.Wall);
			
			orientationOffset = getOrientationPixelOffset(particle.getPosition().getAngleInDegree());
			currentPoint.x += orientationOffset.x;
			currentPoint.y += orientationOffset.y;
			simCoordinates = convertCoordinatesToSimulator(currentPoint);
			i++;
			debugPoints[i] = new DebugGridPoint((short)simCoordinates.x, (short)simCoordinates.y, (byte)100, teambot.communication.DebugGridPointStatus.Valid);
			i++;
		}
		
		return debugPoints;
	}
	
	public DebugGridPoint[] convertSlamMapAndParticlePosition(ProbabilityMap map, Position bestBotPosition, Particle[] particles)
	{
		DebugGridPoint[] debugSlamMap = convertSlamMap(map, bestBotPosition);
		DebugGridPoint[] debugParticles = particlesToMap(particles);
		return ArrayUtils.addAll(debugSlamMap, debugParticles);
	}
	
	private Point convertCoordinatesToSimulator(Point point)
	{
		Point simCoordinates = new Point(point);
		int offsetYBecauseOfCoordinateFlip = 1;
		simCoordinates.y = PathPlanningAgentUpdater.offsetY - ((point.y + offsetYBecauseOfCoordinateFlip) * _cellSize);
		simCoordinates.x = point.x * _cellSize;
		return simCoordinates;
	}
	
	private Point getOrientationPixelOffset(float angle_deg)
	{
		if(angle_deg >= -20 && angle_deg < 20)
			return new Point(1, 0);
		
		if(angle_deg >= 20 && angle_deg < 60)
			return new Point(1, 1);
		
		if(angle_deg >= 60 && angle_deg < 100)
			return new Point(0, 1);
		
		if(angle_deg >= 100 && angle_deg < 140)
			return new Point(-1, 1);
		
		
		if(angle_deg >= 140 || angle_deg < -140)
			return new Point(-1, 0);
		
		if(angle_deg >= -140 && angle_deg < -100)
			return new Point(-1, -1);
		
		if(angle_deg >= -100 && angle_deg < -60)
			return new Point(0, -1);
		
		if(angle_deg >= -60 && angle_deg < -20)
			return new Point(1, -1);
		return null;
	}
	
	public int getCellSize()
	{
		return _cellSize;
	}
	
	public DebugGridPoint[] convertAverageMap(Particle[] particles, ParticleFilter filter)
	{
		ProbabilityMap averageMap = new ProbabilityMap(new BeamProbabilities(0.5f, 0.8f, 0.2f));
		Hashtable<Point, Float> averageTable = averageMap.getMap();
		ProbabilityMap map;
		Hashtable<Point, Float> mapTable;
		Hashtable<Point, Float> weightSumsTable = new Hashtable<Point, Float>(200);
		
		for(Particle particle : particles)
		{
			map = particle.getMap();
			mapTable = map.getMap();
			
			Set<Point> points =  mapTable.keySet();
			for(Point point : points)
			{
				
				if(averageTable.containsKey(point))
				{
					averageTable.put(point, (float) (averageTable.get(point) + mapTable.get(point) * particle.getWeight()));
					weightSumsTable.put(point, particle.getWeight() + weightSumsTable.get(point) );
				}
				else
				{
					averageTable.put(point, mapTable.get(point) * particle.getWeight());
					weightSumsTable.put(point, particle.getWeight());
				}
			}
		}
		
		Set<Point> points =  averageTable.keySet();
		for(Point point : points)
		{
			averageTable.put(point, averageTable.get(point) / weightSumsTable.get(point));
		}
		
		return convertSlamMap(averageMap, new Position(filter.calculateParticleMeanPosition(particles), 0));
	}
}
