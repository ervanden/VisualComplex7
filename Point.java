import java.util.*;
import expression.*;



class Point {
public Complex z;
public Complex w;

public Point(double px, double py){
z=new Complex(px,py);
w=VisualComplex.ComplexFunction.evaluateExpression(z);
}

public Point(){  // used to add a point to an integral shape
z=null;
w=new Complex(0,0);
}

public Complex getZ() { return z; }
public Complex getW() { return w; }
public double getZX() { return z.re(); }
public double getZY() { return z.im(); }
public double getWX() { return w.re(); }
public double getWY() { return w.im(); }
public void setZ(Complex newz) { z=newz; }
public void replaceXY(double x, double y) { z.x=x; z.y=y; }
public void evaluate() { w=VisualComplex.ComplexFunction.evaluateExpression(z); }

}
