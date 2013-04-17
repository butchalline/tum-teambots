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
			if (!_map.containsKey(pointInfo))
				_map.put(pointInfo.getKey(), _probabilities.getLogStartProbability());
			else
			{
				currentValue = _map.get(pointInfo.getKey());
				if (pointInfo.getValue() == Occupation.occupied)
				{
					currentValue += _probabilities.getLogOccupationProbability() - _probabilities.getLogStartProbability();
				}
				else
				{
					currentValue += _probabilities.getLogFreeProbability() - _probabilities.getLogStartProbability();
				}
				
				_map.put(pointInfo.getKey(), currentValue);
			}
		}
	}
	
	public float getProbability(Point point)
	{
		if(_map.contains(point))
			return (float) (1 - 1/(1 + Math.exp(_map.get(point))));
		else
		{
			_map.put(point, 0.5f);
			return 0.5f;
		}
	}
}
