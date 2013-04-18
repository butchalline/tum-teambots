package teambot.pathplanning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import android.graphics.Point;

public class WaveFrontAlg {

	private float makeDistance(Point p1, Point p2) {
		float distance = (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
		return distance;
	}

	public Point[] makePath(Map map, Point start, Point target) {
		HashMap<Point, Integer> visited_fields = new HashMap<Point, Integer>();
		visited_fields.put(start, 0);
		LinkedList<Point> front_fields = new LinkedList<Point>();
		front_fields.add(start);

		boolean target_reached = false;

		while (!front_fields.isEmpty()) {
			Point current = front_fields.removeFirst();
			Integer wave_counter = visited_fields.get(current);

			Point neighbors[] = new Point[4];
			neighbors[0] = new Point(current.x + 1, current.y);
			neighbors[1] = new Point(current.x - 1, current.y);
			neighbors[2] = new Point(current.x, current.y + 1);
			neighbors[3] = new Point(current.x, current.y - 1);

			for (int i = 0; i < 4; i++) {
				if (!visited_fields.containsKey(neighbors[i]) && map.isValidPathPoint(neighbors[i])) {
					visited_fields.put(neighbors[i], wave_counter + 1);
					front_fields.add(neighbors[i]);
					if (neighbors[i].equals(target)) {
						target_reached = true;
						break;
					}
				}
			}
			if (target_reached) {
				break;
			}
		}

		if (target_reached == false) {
			return null;
		} else {
			Point current = target;
			Integer wave_counter = visited_fields.get(current);
			Point path[] = new Point[wave_counter + 1];
			path[wave_counter] = current;

			Point neighbors[] = new Point[4];

			while (!current.equals(start)) {

				neighbors[0] = new Point(current.x + 1, current.y);
				neighbors[1] = new Point(current.x - 1, current.y);
				neighbors[2] = new Point(current.x, current.y + 1);
				neighbors[3] = new Point(current.x, current.y - 1);

				for (int i = 0; i < 4; i++) {
					if (visited_fields.containsKey(neighbors[i]) && visited_fields.get(neighbors[i]) == wave_counter - 1) {
						wave_counter--;
						current = neighbors[i];
						path[wave_counter] = current;
						break;
					}
				}
			}
			return path;
		}
	}

	public Region makeUndiscoveredRegion(Map map, Point start, float radius) {
		HashSet<Point> visited_fields = new HashSet<Point>();
		visited_fields.add(start);
		LinkedList<Point> front_fields = new LinkedList<Point>();
		front_fields.add(start);

		int min_x, max_x, min_y, max_y;
		min_x = max_x = start.x;
		min_y = max_y = start.y;
		int counter = 0;

		while (!front_fields.isEmpty()) {
			Point current = front_fields.removeFirst();
			if (this.makeDistance(current, start) < radius) {
				if (map.getOccupation(current) == Occupation.unknown) {
					counter++;
					min_x = Math.min(min_x, current.x);
					max_x = Math.max(max_x, current.x);
					min_y = Math.min(min_y, current.y);
					max_y = Math.max(max_y, current.y);
				}
				Point neighbors[] = new Point[4];
				neighbors[0] = new Point(current.x + 1, current.y);
				neighbors[1] = new Point(current.x - 1, current.y);
				neighbors[2] = new Point(current.x, current.y + 1);
				neighbors[3] = new Point(current.x, current.y - 1);

				for (int i = 0; i < 4; i++) {
					if (!visited_fields.contains(neighbors[i]) && (map.getOccupation(neighbors[i]) != Occupation.occupied)) {
						visited_fields.add(neighbors[i]);
						front_fields.add(neighbors[i]);
					}
				}
			}
		}

		Region region = new Region(counter, min_x, max_x, min_y, max_y);
		return region;
	}
}
