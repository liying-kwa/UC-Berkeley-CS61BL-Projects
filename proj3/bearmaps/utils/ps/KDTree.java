package bearmaps.utils.ps;

import java.util.List;

public class KDTree implements PointSet {

    private KDTreeNode root = null;
    int k = 2;

    private double bestDist;

    /* Constructs a KDTree using POINTS. You can assume POINTS contains at least one
       Point object. */
    public KDTree(List<Point> points) {
        for (Point p : points) {
            root = insert(root, p, true);
        }
    }

    private KDTreeNode insert(KDTreeNode node, Point point, boolean xAxis) {
        if (node == null) {
            return new KDTreeNode(point);
        } else {
            if (comparePoint(point, node.point, xAxis) < 0) {
                node.left = insert(node.left, point, !xAxis);
            } else {
                node.right = insert(node.right, point, !xAxis);
            }
        }
        return node;
    }

    private int comparePoint(Point p1, Point p2, boolean xAxis) {
        if (xAxis) {
            if (Double.compare(p1.getX(), p2.getX()) != 0) {
                return Double.compare(p1.getX(), p2.getX());
            } else {
                return Double.compare(p1.getY(), p2.getY());
            }
        } else {
            if (Double.compare(p1.getY(), p2.getY()) != 0) {
                return Double.compare(p1.getY(), p2.getY());
            } else {
                return Double.compare(p1.getX(), p2.getX());
            }
        }
    }

    /* Returns the closest Point to the inputted X and Y coordinates. This method
       should run in O(log N) time on average, where N is the number of POINTS. */
    public Point nearest(double x, double y) {
        return nearestHelper(root, new Point(x, y), true, null).point;
    }

    private KDTreeNode nearestHelper(KDTreeNode node, Point p, boolean xAxis, KDTreeNode best) {

        if (node == null) {
            return best;
        }
        if (best == null || Double.compare(Point.distance(node.point, p), bestDist) < 0) {
            best = node;
            bestDist = Point.distance(best.point, p);
        }
        KDTreeNode goodSide, badSide;
        if (comparePoint(p, node.point, xAxis) < 0) {
            goodSide = node.left;
            badSide = node.right;
        } else {
            goodSide = node.right;
            badSide = node.left;
        }

        best = nearestHelper(goodSide, p, !xAxis, best);

        if (Double.compare(bestDist, otherDist(p, node.point, xAxis)) > 0) {
            best = nearestHelper(badSide, p, !xAxis, best);
        }

        return best;
    }

    private double otherDist(Point p1, Point p2, boolean xAxis) {
        if (xAxis) {
            return Point.distance(p1, new Point(p2.getX(), p1.getY()));
        } else {
            return Point.distance(p1, new Point(p1.getX(), p2.getY()));
        }
    }

    private class KDTreeNode {

        private Point point;
        private KDTreeNode left;
        private KDTreeNode right;

        // If you want to add any more instance variables, put them here!

        KDTreeNode(Point p) {
            this.point = p;
        }

        KDTreeNode(Point p, KDTreeNode left, KDTreeNode right) {
            this.point = p;
            this.left = left;
            this.right = right;
        }

        Point point() {
            return point;
        }

        KDTreeNode left() {
            return left;
        }

        KDTreeNode right() {
            return right;
        }

        // If you want to add any more methods, put them here!

    }
}
