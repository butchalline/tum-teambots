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

	void updateMap(LinkedList<SimpleEntry<Point, Occupation>> points)
	{
		Float currentValue;

		for (SimpleEntry<Point, Occupation> point : points)
		{
			if (!_map.containsKey(point))
				_map.put(point.getKey(), _probabilities.getLogStartProbability());
			else
			{
				currentValue = _map.get(point);
				if (point.getValue() == Occupation.occupied)
				{
					currentValue += _probabilities.getLogOccupationProbability() - _probabilities.getLogStartProbability();
				}
				else
				{
					currentValue += _probabilities.getLogFreeProbability() - _probabilities.getLogStartProbability();
				}
				
				_map.put(point.getKey(), currentValue);
			}
		}
	}
	
	public float getProbability(Point point)
	{
		return (float) (1 - 1/(1 + Math.exp(_map.get(point))));
	}
}
