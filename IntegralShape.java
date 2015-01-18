import java.util.ArrayList;



public class IntegralShape extends Shape {
    
    Shape integralOf;

    public IntegralShape(String itsname, Shape iOf) {
        pList = new ArrayList<Point>();
        pList.clear();
        label = itsname;
        isIntegral = true;
        integralOf = iOf;
        iOf.hasIntegral = true;
        iOf.integral = this;
    }
}
