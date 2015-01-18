import java.util.*;



class PointShape extends Shape {  // shape that represents a complex point

    public PointShape(String itsname){
pList = new ArrayList<Point>();
pList.clear();
label=itsname;
isComplexPoint=true;
}

public double getX() { return this.pList.get(0).getZX(); }
public double getY() { return this.pList.get(0).getZY(); }
public double getFx() { return this.pList.get(0).getWX(); }
public double getFy() { return this.pList.get(0).getWY(); }
public Point getPoint() { return this.pList.get(0); }
public String getName() { return label; }

}