package teambot.pathplanning;

import android.graphics.Point;

public class Test {

	public static void printPoints(Point[] points)
	{
		if (points == null)
			System.out.println("null");
		else
		{
			for (int i = 0; i < points.length; i++)
			{
				System.out.println("(" + points[i].x + ", " + points[i].y + ")");
			}
		}
	}

	public static void clearMap(Map map, int size_x, int size_y, Occupation occupation)
	{
		for (int i = 0; i < size_x; i++)
			for (int j = 0; j < size_y; j++)
				map.setOccupation(new Point(i,j), occupation);
	}

	public static void main(String [ ] args)
	{
		Map map = new Map(1, 2);
		WaveFrontAlg wfa = new WaveFrontAlg();

		System.out.println("PATHFINDING TESTS \n");
		Point[] path;

		System.out.println("----TEST1----");
		Test.clearMap(map, 10, 10, Occupation.free);
		path = wfa.makePath(map, new Point(0,0), new Point(9,9));
		Test.printPoints(path);

		System.out.println("----TEST2----");
		Test.clearMap(map, 10, 10, Occupation.free);
		map.setOccupation(new Point(9,9), Occupation.occupied);
		path = wfa.makePath(map, new Point(0,0), new Point(9,9));
		Test.printPoints(path);

		System.out.println("----TEST3----");
		Test.clearMap(map, 10, 10, Occupation.free);
		for (int i = 0; i < 10; i++)
			map.setOccupation(new Point(5,i), Occupation.occupied);
		path = wfa.makePath(map, new Point(0,0), new Point(9,9));
		Test.printPoints(path);

		System.out.println("----TEST4----");
		Test.clearMap(map, 10, 10, Occupation.free);
		for (int i = 0; i < 10; i++)
			map.setOccupation(new Point(5,i), Occupation.unknown);
		path = wfa.makePath(map, new Point(0,0), new Point(9,9));
		Test.printPoints(path);

	    System.out.println("----TEST5----");
	    Test.clearMap(map, 10, 10, Occupation.free);
		for (int i = 0; i < 9; i++)
			map.setOccupation(new Point(4,i), Occupation.occupied);
		for (int i = 1; i < 10; i++)
			map.setOccupation(new Point(6,i), Occupation.occupied);
		path = wfa.makePath(map, new Point(0,0), new Point(9,9));
		Test.printPoints(path);


		System.out.println("\nUNDISCOVERED REGION TESTS \n");
		Region region;

		System.out.println("----TEST1----");
		Test.clearMap(map, 10, 10, Occupation.free);
		region = wfa.makeUndiscoveredRegion(map, new Point(5,5), 3);
		System.out.println(region.num_fields);

		System.out.println("----TEST2----");
		Test.clearMap(map, 10, 10, Occupation.unknown);
		region = wfa.makeUndiscoveredRegion(map, new Point(5,5), 3);
		System.out.println(region.num_fields);

		System.out.println("----TEST3----");
		Test.clearMap(map, 10, 10, Occupation.unknown);
		for (int i = 0; i < 10; i++)
			map.setOccupation(new Point(6,i), Occupation.free);
		region = wfa.makeUndiscoveredRegion(map, new Point(5,5), 3);
		System.out.println(region.num_fields);

		System.out.println("----TEST4----");
		Test.clearMap(map, 10, 10, Occupation.unknown);
		for (int i = 0; i < 10; i++)
			map.setOccupation(new Point(6,i), Occupation.occupied);
		region = wfa.makeUndiscoveredRegion(map, new Point(5,5), 3);
		System.out.println(region.num_fields);

		System.out.println("----TEST5----");
		Test.clearMap(map, 10, 10, Occupation.unknown);
		for (int i = 3; i < 8; i++)
			map.setOccupation(new Point(6,i), Occupation.occupied);
		region = wfa.makeUndiscoveredRegion(map, new Point(5,5), 3);
		System.out.println(region.num_fields);

		System.out.println("----TEST6----");
		Test.clearMap(map, 10, 10, Occupation.unknown);
		for (int i = 4; i < 8; i++)
			map.setOccupation(new Point(6,i), Occupation.occupied);
		region = wfa.makeUndiscoveredRegion(map, new Point(5,5), 3);
		System.out.println(region.num_fields);

		System.out.println("----TEST7----");
		Test.clearMap(map, 10, 10, Occupation.unknown);
		region = wfa.makeUndiscoveredRegion(map, new Point(0,0), 3);
		System.out.println(region.num_fields);



		System.out.println("\nGET FREE POINTS TEST \n");
		Point[] freePoints;

		System.out.println("----TEST1----");
		map = new Map(1, 2);
		Test.clearMap(map, 5, 5, Occupation.free);
		for (int i = 0; i < 5; i++)
			map.setOccupation(new Point(2,i), Occupation.occupied);
		for (int i = 0; i < 5; i++)
			map.setOccupation(new Point(i,2), Occupation.unknown);
		freePoints = map.getFreePathPoints();
		Test.printPoints(freePoints);


	    return;

	}

}
