package teambot.common.slam;

import java.util.AbstractMap.SimpleEntry;
import java.util.Hashtable;
import java.util.LinkedList;

import teambot.common.slam.BeamProbabilities;
import teambot.common.slam.Occupation;
import teambot.common.slam.ProbabilityMap;

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
		synchronized (map)
		{
			_map = new Hashtable<Point, Float>(map._map);
			_probabilities = new BeamProbabilities(map._probabilities);	
		}		
	}

	synchronized void update(LinkedList<SimpleEntry<Point, Occupation>> points)
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
				} else
				{
					currentValue += _probabilities.getLogOddFree() - _probabilities.getLogOddStart();
				}

				_map.put(pointInfo.getKey(), currentValue);
			}
		}
	}

	public synchronized void addPoint(Point point, float probability)
	{
		_map.put(point, (float) Math.log(probability / (1 - probability)));
	}

	public synchronized void addPoint(Point point)
	{
		_map.put(point, _probabilities.getLogOddStart());
	}

	public synchronized Float getProbability(Point point)
	{
		if (_map.containsKey(point))
			return (float) (1 - 1 / (1 + Math.exp(_map.get(point))));
		else
			return null;
	}

	public synchronized Hashtable<Point, Float> getMap()
	{
		return _map;
	}
}
