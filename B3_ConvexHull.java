public class B3_ConvexHull{
    //structure to represent a point
    public static class Point {
        public double x;
        public double y;
    }

    //sort points by x coordinate then by y coordinate function
    public static void sortPoints(Point[] points) {
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                if (points[i].x > points[j].x || (points[i].x == points[j].x && points[i].y > points[j].y)) {
                    Point temp = points[i];
                    points[i] = points[j];
                    points[j] = temp;
                }
            }
        }
    }

    //function to calculate the cross product
    public static double crossProduct(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    //function to find the convex hull of a set of points using Monotone Chain algorithm
    public static Point[] convexHull(Point[] points) {
        //sort points by x coordinate then by y coordinate
        sortPoints(points);

        //initialize the upper and lower hulls
        Point[] upperHull = new Point[points.length];
        Point[] lowerHull = new Point[points.length];

        //initialize the upper and lower hull sizes
        int upperHullSize = 0;
        int lowerHullSize = 0;

        //find the upper hull
        for (int i = 0; i < points.length; i++) {
            while (upperHullSize >= 2 && crossProduct(upperHull[upperHullSize - 2], upperHull[upperHullSize - 1], points[i]) <= 0) {
                upperHullSize--;
            }
            upperHull[upperHullSize++] = points[i];
        }

        //find the lower hull
        for (int i = points.length - 1; i >= 0; i--) {
            while (lowerHullSize >= 2 && crossProduct(lowerHull[lowerHullSize - 2], lowerHull[lowerHullSize - 1], points[i]) <= 0) {
                lowerHullSize--;
            }
            lowerHull[lowerHullSize++] = points[i];
        }

        //initialize the convex hull
        Point[] convexHull = new Point[upperHullSize + lowerHullSize - 2];

        //copy the upper hull to the convex hull
        for (int i = 0; i < upperHullSize - 1; i++) {
            convexHull[i] = upperHull[i];
        }

        //copy the lower hull to the convex hull
        for (int i = 0; i < lowerHullSize - 1; i++) {
            convexHull[upperHullSize + i - 1] = lowerHull[i];
        }

        return convexHull;
    }

    //function to read input from "B3_input.txt" file using System.in
    public static Point[] readInput() {
        //initialize the number of points
        int n;

        //initialize the points array
        Point[] points;

        //read the number of points
        n = 6;

        //initialize the points array
        points = new Point[n];

        //read the points
        points[0] = new Point();
        points[0].x = 2;
        points[0].y = 1;

        points[1] = new Point();
        points[1].x = 1;
        points[1].y = 3;

        points[2] = new Point();
        points[2].x = 2;
        points[2].y = 3;

        points[3] = new Point();
        points[3].x = 2;
        points[3].y = 4;

        points[4] = new Point();
        points[4].x = 4;
        points[4].y = 1;

        points[5] = new Point();
        points[5].x = 4;
        points[5].y = 3;


        return points;
    }

    //function to write output to "B3_output.txt" file using System.out
    public static void writeOutput(Point[] convexHull) {
        //print out convex hull points to the console to test first
        for (Point convexHull1 : convexHull) {
            System.out.println(convexHull1.x + " " + convexHull1.y);
        }
    }
    

    public static void main(String[] args) {
        Point[] points = readInput();
        Point[] convexHull = convexHull(points);
        writeOutput(convexHull);
    }
}