package teambot.slam;

import java.util.AbstractMap.SimpleEntry;
import java.util.Hashtable;
import java.util.LinkedList;

import teambot.pathplanning.Occupation;
import android.graphics.Point;

public class ProbabilityMap
{
	Hashtable<Point, Float> _map = new Hashtable<Point, Float>();
	BeamProbabilities _probabilities;
	
	public ProbabilityMap(BeamProbabilities probabilities)
	{
		_probabilities = probabilities;
	}

	public ProbabilityMap(ProbabilityMap map)
	{
		_map = new Hashtable<Point, Float>(map._map);
		_probabilities = new BeamProbabilities(map._probabilities);
	}
	
	void update(LinkedList<SimpleEntry<Point, Occupation>> points)
	{
		Float currentValue;

		for (SimpleEntry<Point, Occupation> pointInfo : points)
		{
			if (!_map.containsKey(pointInfo.getKey()))
				_map.put(pointInfo.getKey(), _probabilities.getLogOddStart());
			else
			{
				currentValue = _map.get(pointInfo.getKey());
				if (pointInfo.getValue() == Occupation.occupied)
				{
					currentValue += _probabilities.getLogOddOccupation() - _probabilities.getLogOddStart();
				}
				else
				{
					currentValue += _probabilities.getLogOddFree() - _probabilities.getLogOddStart();
				}
				
				_map.put(pointInfo.getKey(), currentValue);
			}
		}
	}
	
	public void addPoint(Point point, float probability)
	{
		_map.put(point, probability);
	}
	
	
	public void addPoint(Point point)
	{
		_map.put(point, _probabilities.getLogOddStart());
	}
	
	public Float getProbability(Point point)
	{
		if(_map.containsKey(point))
			return (float) (1 - 1/(1 + Math.exp(_map.get(point))));
		else
			return null;
	}
	
	public Hashtable<Point, Float> getMap()
	{
		return _map;
	}
}
