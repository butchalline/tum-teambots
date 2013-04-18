package teambot.pathplanning;

import java.util.ArrayDeque;
import java.util.Map.Entry;

import android.graphics.Point;
import android.graphics.PointF;

public class Agent {

	private final Map map;
	private Point target;
	private Point position;
	private final int _distanceFromWall_cells;
	private final float field_size;

	private final WaveFrontAlg waveFrontAlg;

	public Agent(int size_x, int size_y, float field_size, int distanceFromWall_cells) {
		this.field_size = field_size;
		this.target = null;
		this.position = new Point(0, 0);
		_distanceFromWall_cells = distanceFromWall_cells;
		this.map = new Map(2, distanceFromWall_cells);
		this.waveFrontAlg = new WaveFrontAlg();
	}

	public synchronized ArrayDeque<PointF> makePath()
	{
		Point[] freePoints = map.getFreePathPoints();
		Point[] bestPath = null;
		float bestUtility = Float.NEGATIVE_INFINITY;

		for (int i = 0; i < freePoints.length; i++)
		{
			Region region = this.waveFrontAlg.makeUndiscoveredRegion(this.map, freePoints[i], _distanceFromWall_cells+2);
			Point[] path = this.waveFrontAlg.makePath(this.map, this.position, freePoints[i]);
			if(path == null)
				continue;
			float utility = region.num_fields / (float) path.length;
			if (freePoints[i] == target)
			{
				utility *= 2;
			}
			if (utility > bestUtility)
			{
				bestUtility = utility;
				bestPath = path;
			}
		}

		printMapInConsole(null);
		if(bestPath != null)
			printMapInConsole(bestPath);
		System.out.println("Best Utility: " + bestUtility);

		if (bestPath != null && bestPath.length != 0)
		{
			if (this.position == bestPath[bestPath.length-1])
			{
				return null;
			}
			this.target = bestPath[bestPath.length-1];
		}
		else
			return null;

		ArrayDeque<PointF> bestPathF = new ArrayDeque<PointF>(bestPath.length);
		boolean directionIsVertical = true;
		if(bestPath.length >= 2 && bestPath[0].y == bestPath[1].y)
			directionIsVertical = false;

		for (int i = 1; i < bestPath.length-1; i++)
		{
			if(directionIsVertical && bestPath[i].x == bestPath[i+1].x)
				continue;

			if(!directionIsVertical && bestPath[i].y == bestPath[i+1].y)
				continue;

			System.out.println("Path field " + i + ": " + bestPath[i].x + "x" + bestPath[i].y);
			bestPathF.add(new PointF((bestPath[i].x + 0.5f) * this.field_size, (bestPath[i].y + 0.5f) * this.field_size));
			directionIsVertical = !directionIsVertical;
		}

		bestPathF.add(new PointF((bestPath[bestPath.length - 1].x + 0.5f) * this.field_size, (bestPath[bestPath.length - 1].y + 0.5f) * this.field_size));
		System.out.println("Path field " + (bestPath.length - 1) + ": " + bestPath[bestPath.length - 1].x + "x" + bestPath[bestPath.length - 1].y);

		return bestPathF;
	}

	void printMapInConsole(Point[] path) {

		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = minX;
		int maxY = maxX;

		for(Entry<Point, Field> field : map.getMapGrid())
		{
			if(field.getKey().x < minX)
				minX = field.getKey().x;
			if(field.getKey().x > maxX)
				maxX = field.getKey().x;

			if(field.getKey().y < minY)
				minY = field.getKey().y;
			if(field.getKey().y > maxY)
				maxY = field.getKey().y;
		}

		int rangeX = maxX - minX + 1;
		int rangeY = maxY - minY + 1;

		char[][] outputMap = new char[rangeX][rangeY];

		for(int x = 0; x < rangeX; ++x)
		{
			for(int y = 0; y < rangeY; ++y)
			{
				Occupation occu = map.getOccupation(new Point(x + minX, y + minY));
				boolean isValidPathPoint = map.isValidPathPoint(new Point(x + minX, y + minY));

				switch (occu) {
				case occupied: outputMap[x][y] = '#';
					break;
				case free:
				{
					if(isValidPathPoint)
						outputMap[x][y] = '0';
					else
						outputMap[x][y] = '?';
					break;
				}
				case unknown: outputMap[x][y] = ' ';
					break;
				}
			}
		}

		if(path != null)
		{
			for(int i = 0; i < path.length; i++)
			{
				if(outputMap[path[i].x-minX][path[i].y-minY] != '0')
					outputMap[path[i].x-minX][path[i].y-minY] = '!';
				else
					outputMap[path[i].x-minX][path[i].y-minY] = Integer.toString(i+1).charAt(0);
			}
		}

		System.out.println("---------------------Map----------------------");

		for(int y = rangeY-1; y >= 0; --y)
		{
			for(int x = 0; x < rangeX; ++x)
			{
				System.out.print(outputMap[x][y]);
				System.out.print(' ');
			}
			System.out.println("");
		}
		System.out.println("----------------------------------------------");
	}

	public Map getMap()
	{
		return map;
	}

	/**
	 *
	 * @param robot_position
	 *            in mm
	 * @param robot_angle
	 *            in rad
	 * @param dist_left
	 *            measurement of left sensor in cm
	 * @param dist_mid
	 *            measurement of mid sensor in cm
	 * @param dist_right
	 *            measurement of right sensor in cm
	 */

	public synchronized void addMeasurement(PointF robot_position_mm, float robot_angle, float dist_left_cm, float dist_mid_cm,
			float dist_right_cm) {
		PointF robot_position = new PointF();
		robot_position.x = robot_position_mm.x / this.field_size;
		robot_position.y = robot_position_mm.y / this.field_size;

		PointF left_sensor_pos, mid_sensor_pos, right_sensor_pos;
		float front_offset = 90.0f / this.field_size;
		float side_offset = 96.25f / this.field_size;

		// The mid sensor is shifted from the center of the robot in the
		// direction the robot faces (front offset)
		// The left and right sensors are also shifted to the side (side offset)
		mid_sensor_pos = new PointF(robot_position.x + (float) Math.cos(robot_angle) * front_offset, robot_position.y
				+ (float) Math.sin(robot_angle) * front_offset);
		left_sensor_pos = new PointF(robot_position.x + (float) Math.cos(robot_angle + Math.PI / 2) * side_offset
				+ (float) Math.cos(robot_angle) * front_offset, robot_position.y + (float) Math.sin(robot_angle + Math.PI / 2)
				* side_offset + (float) Math.sin(robot_angle) * front_offset);
		right_sensor_pos = new PointF(robot_position.x + (float) Math.cos(robot_angle - Math.PI / 2) * side_offset
				+ (float) Math.cos(robot_angle) * front_offset, robot_position.y + (float) Math.sin(robot_angle - Math.PI / 2)
				* side_offset + (float) Math.sin(robot_angle) * front_offset);

		float max_range_left = 200 / this.field_size;
		float max_range_mid = 1500 / this.field_size;
		float max_range_right = 200 / this.field_size;

		float dist_left = (dist_left_cm * 10) / this.field_size;
		float dist_mid = (dist_mid_cm * 10) / this.field_size;
		float dist_right = (dist_right_cm * 10) / this.field_size;

//		this.map.addMeasurement(left_sensor_pos, robot_angle, dist_left, max_range_left);
		this.map.addMeasurement(mid_sensor_pos, robot_angle, dist_mid, max_range_mid);
//		this.map.addMeasurement(right_sensor_pos, robot_angle, dist_right, max_range_right);

		this.position = new Point((int)(robot_position.x), (int)(robot_position.y));
	}
}
