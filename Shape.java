import java.util.*;

class Shape {

    public String label = "";
    public java.util.List<Point> pList;

    public boolean isComplexPoint = false;
    public boolean isParameter = false;
    public boolean isIntegral = false;
    public boolean hasIntegral = false;
    public Shape integral = null;

    public boolean isSelected = false;
    public boolean isPreSelected = false;
    public boolean isPreUnselected = false;

    public Shape() {
        pList = new ArrayList<Point>();
        pList.clear();
    }

    public void clear() {
        pList.clear();
    }

    public void addPoint(double x, double y) {
        pList.add(new Point(x, y));
    }

    public void addPoint() {   // add point to integral shape
        pList.add(new Point());

    }

    public int nrPoints() {
        return pList.size();
    }

    public Point lastPoint() {
        return pList.get(pList.size() - 1);
    }

} // class Shape
