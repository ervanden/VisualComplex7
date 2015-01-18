import expression.Complex;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

class Drawing {

    java.util.List<Shape> sList;
    java.util.List<Shape> parameterList;  // parameters are also in sList
    double areaCursorX1, areaCursorY1, areaCursorX2, areaCursorY2;
    boolean areaCursorOn = false;

    public boolean dotsVisible = false;
    public boolean linesVisible = true;
    public boolean scaleVisible = true;
    public boolean snapToGrid = false;
    public boolean shapeIntegral = false;

    public double xmin, fxmin, xmax, fxmax, ymin, fymin, ymax, fymax; // calculated by method setMinMAx()

    ;
    int complexPointCounter; // used to construct names z1, z2, ...
    int shapeCounter;  // used to construct names shape1, shape2,...
    int integralCounter;

    public Drawing() {
        sList = new ArrayList<Shape>();
        parameterList = new ArrayList<Shape>();
        sList.clear();
        parameterList.clear();
        complexPointCounter = 1;
        shapeCounter = 1;
        integralCounter = 1;
    }

    public void clear() {
        sList.retainAll(parameterList);
        complexPointCounter = 1;
        shapeCounter = 1;
    }

    public void clearSelection() {
        // first select integrals of selected shapes
        // then delete all selected shapes

        Iterator<Shape> itr = sList.iterator();

        while (itr.hasNext()) {
            Shape s = itr.next();
            if (s.isSelected && s.hasIntegral) {
                s.integral.isSelected = true;
            }
        }

        itr = sList.iterator();
        while (itr.hasNext()) {
            Shape s = itr.next();
            if (s.isSelected) {
                itr.remove();
            }

        }
    }

    public void deleteShape(Shape s) {
        sList.remove(s);
    }

    public void deleteIntegralShapes() {

        Iterator<Shape> itr = sList.iterator();

        while (itr.hasNext()) {
            Shape s = itr.next();
            System.out.println(s.label);
            s.hasIntegral = false;
            s.integral = null;
            if (s.isIntegral) {
                itr.remove();
            }

        }
    }

    public void areaCursor(boolean cursorOn, double ax1, double ay1, double ax2, double ay2) {
        areaCursorOn = cursorOn;
        areaCursorX1 = ax1;
        areaCursorY1 = ay1;
        areaCursorX2 = ax2;
        areaCursorY2 = ay2;
    }

    public void draw(Transform t) {

        BasicStroke stroke0 = new BasicStroke();     // default dunne lijn
        BasicStroke stroke2 = new BasicStroke(2);    // dikkere lijn voor x en y as
        Graphics2D g2 = (Graphics2D) t.graphics;

        if (t.zPlane && snapToGrid) {
            t.gridLines();
        }

        t.axes();

        if (areaCursorOn && t.zPlane) {
            g2.setColor(Color.gray);
            t.line(areaCursorX1, areaCursorY1, areaCursorX2, areaCursorY1);
            t.line(areaCursorX2, areaCursorY1, areaCursorX2, areaCursorY2);
            t.line(areaCursorX2, areaCursorY2, areaCursorX1, areaCursorY2);
            t.line(areaCursorX1, areaCursorY2, areaCursorX1, areaCursorY1);
        }

        Point xyprev;
        for (Shape s : sList) {

            if ((s.isSelected || s.isPreSelected) && !s.isPreUnselected) {
                t.graphics.setColor(Color.RED);
                g2.setStroke(stroke2);
            } else if (s.isParameter) {
                t.graphics.setColor(Color.BLUE);
            } else {
                t.graphics.setColor(Color.BLACK);
                g2.setStroke(stroke0);
            };

            if (s.isIntegral) {
                t.graphics.setColor(Color.GREEN);
                g2.setStroke(stroke2);
            };

            if (s.getClass().getName().equals("PointShape")) {

                PointShape ps = (PointShape) s;
                if (t.zPlane) {
                    t.complexPoint(ps.getName(), ps.getX(), ps.getY());

                };

                if (t.wPlane) {
                    t.complexPoint(ps.getName(), ps.getFx(), ps.getFy());
                };

            } else {   // not a point shape

                if (linesVisible) {
                    xyprev = null;
                    for (Point xy : s.pList) {
                        if (xyprev != null) {
                            if (t.zPlane && !s.isIntegral) {
                                t.line(xyprev.getZX(), xyprev.getZY(), xy.getZX(), xy.getZY());
                            }
                            if (t.wPlane) {
                                t.line(xyprev.getWX(), xyprev.getWY(), xy.getWX(), xy.getWY());
                            }
                        };
                        xyprev = xy;
                    };
                };

                if (dotsVisible) {
                    for (Point xy : s.pList) {
                        if (t.zPlane && !s.isIntegral) {
                            t.dot(xy.getZX(), xy.getZY());
                        }
                        if (t.wPlane) {
                            t.dot(xy.getWX(), xy.getWY());
                        }
                    };
                };

            }

        } // for

    }  // draw

    public void setMinMax() {

        xmax = Double.NEGATIVE_INFINITY;
        xmin = Double.POSITIVE_INFINITY;
        ymax = Double.NEGATIVE_INFINITY;
        ymin = Double.POSITIVE_INFINITY;
        fxmax = Double.NEGATIVE_INFINITY;
        fxmin = Double.POSITIVE_INFINITY;
        fymax = Double.NEGATIVE_INFINITY;
        fymin = Double.POSITIVE_INFINITY;

        for (Shape s : sList) {

            for (Point xy : s.pList) {
                if (xy.getZX() < xmin) {
                    xmin = xy.getZX();
                }
                if (xy.getZX() > xmax) {
                    xmax = xy.getZX();
                }
                if (xy.getZY() < ymin) {
                    ymin = xy.getZY();
                }
                if (xy.getZY() > ymax) {
                    ymax = xy.getZY();
                }

                if (xy.getWX() < fxmin) {
                    fxmin = xy.getWX();
                }
                if (xy.getWX() > fxmax) {
                    fxmax = xy.getWX();
                }
                if (xy.getWY() < fymin) {
                    fymin = xy.getWY();
                }
                if (xy.getWY() > fymax) {
                    fymax = xy.getWY();
                }
            };

        }
    }

    public PointShape closestPointShape(double x, double y) {
        PointShape psmin = null;
        double dmin = Double.POSITIVE_INFINITY;
        for (Shape s : sList) {

            if (s.getClass().getName().equals("PointShape")) {
                PointShape ps = (PointShape) s;
                double x1 = ps.getX();
                double y1 = ps.getY();
                if ((x - x1) * (x - x1) + (y - y1) * (y - y1) < dmin) {
                    dmin = (x - x1) * (x - x1) + (y - y1) * (y - y1);
                    psmin = ps;
                };
            }

        }; // for
        return psmin;
    }

    public Shape closestShape(double x, double y) {
        Shape smin = null;
        double dmin = Double.POSITIVE_INFINITY;
        for (Shape s : sList) {

            if (s.getClass().getName().equals("Shape")) {

                for (Point xy : s.pList) {
                    double x1 = xy.getZX();
                    double y1 = xy.getZY();
                    if ((x - x1) * (x - x1) + (y - y1) * (y - y1) < dmin) {
                        dmin = (x - x1) * (x - x1) + (y - y1) * (y - y1);
                        smin = s;
                    };
                };

            }

        }; // for
        return smin;
    }

    public Point findComplexPointByName(String name) {
        for (Shape s : sList) {
            if (s.label.equals(name)) {
                if (s.getClass().getName().equals("PointShape")) {
                    System.out.println("found complex point " + name + " x=" + s.pList.get(0).getZX() + " y= " + s.pList.get(0).getZY());
                    // assuming that this method is only called from linkExpression
                    s.isParameter = true;
                    parameterList.add(s);
                    return s.pList.get(0);
                }
            }
        };
        return null;
    }

    public Shape addShape() {
        Shape s = new Shape();
        s.label = "shape" + shapeCounter;
        shapeCounter++;
        sList.add(s);
        return s;
    }

    public PointShape addPointShape() {
        PointShape s = new PointShape("z" + complexPointCounter);
        complexPointCounter++;
        sList.add(s);
        return s;
    }

    public IntegralShape addIntegralShape(Shape integralOf) {
        IntegralShape s = new IntegralShape("i" + integralCounter, integralOf);
        integralCounter++;
        sList.add(s);
        return s;
    }

    public PointShape addParameter(String itsname) {
        PointShape s = new PointShape(itsname);
        s.isParameter = true;
        sList.add(s);
        parameterList.add(s);
        return s;
    }

    public void turnParametersIntoPoints() {
        parameterList.clear();
        for (Shape s : sList) {
            s.isParameter = false;

        };
    }

    public void moveShapeRelative(Shape s, double dx, double dy) {
        for (Point xy : s.pList) {
            xy.replaceXY(xy.getZX() + dx, xy.getZY() + dy);
        };
        evaluate(s);
    }  // moveRelativeShape

    public void moveShapesRelative(String l, double dx, double dy) {

// move the shape or shapes with label l 
// if l == "all" then move everything except parameter points
// if l == "selection" then move only selected shapes
        for (Shape s : sList) {
            if ((l.equals("all") && !s.isParameter)
                    || (l.equals("selection") && s.isSelected)
                    || (s.label == l)) {
                for (Point xy : s.pList) {
                    xy.replaceXY(xy.getZX() + dx, xy.getZY() + dy);
                };
                evaluate(s);
            };
        }
    }  // moveShapesRelative

    public void evaluateDrawing() {

        for (Shape s : sList) {
            evaluate(s);
        }
    }

    public void evaluate(Shape s) {

//        System.out.println("evaluate " + s.label);
// evaluate a shape and its integral if it has one
        if (!s.isIntegral) {
            for (Point xy : s.pList) {
                xy.evaluate();
            };

            if (s.hasIntegral) {

                // integral. Delete and rebuild the list of points
                IntegralShape si = (IntegralShape) s.integral;
                si.pList.clear();

                boolean firstPoint = true;
                double x = 0, y = 0, xprev = 0, yprev = 0;

                for (Point xy : s.pList) {

                    if (firstPoint) {
                        // z is irrelevant for integralShape, only w
                        x = xy.z.x;
                        y = xy.z.y;
                        si.addPoint(x, y);
                        si.lastPoint().w.x = 0;
                        si.lastPoint().w.y = 0;
                    }
                    if (!firstPoint) {
                        x = xy.z.x;
                        y = xy.z.y;
                        Complex delta = new Complex(x - xprev, y - yprev);
                        Complex midpoint = new Complex((x + xprev) / 2, (y + yprev) / 2);

                        Complex wi = VisualComplex.ComplexFunction.evaluateExpression(midpoint).
                                mult(delta).add(si.lastPoint().w);
                        si.addPoint(x, y);
                        si.lastPoint().w.x = wi.x;
                        si.lastPoint().w.y = wi.y;
                    };

                    xprev = x;
                    yprev = y;
                    firstPoint = false;
                };
            }
        }
    }  // evaluate

    public int selectShapes(double x, double y, double minPixelDist) {
// find points close to x,y and return their number 
// It is also the number of shapes since very point belongs to exactly one shape
// Select the shapes

        int nrs;
        Shape selectedShape = null;
        double minPixelDistSquare = minPixelDist * minPixelDist;

        nrs = 0;
        for (Shape s : sList) {
            for (Point xy : s.pList) {
                if (((xy.z.x - x) * (xy.z.x - x) + (xy.z.y - y) * (xy.z.y - y)) < minPixelDistSquare) {
                    nrs = nrs + 1;
                    s.isSelected = true;
                    selectedShape = s;
                };
            };
        };

        if (nrs == 0) {
            System.out.println("No shapes selected");
            return 0;
        } else {
            System.out.println(nrs + " shapes selected ");
            return nrs;
        }

    }

    public int unselectShapes(double x, double y, double minPixelDist) {
// find points close to x,y and return their number 
// It is also the number of shapes since very point belongs to exactly one shape
// Unselect the shapes

        int nrs;
        Shape selectedShape = null;
        double minPixelDistSquare = minPixelDist * minPixelDist;

        nrs = 0;
        for (Shape s : sList) {
            for (Point xy : s.pList) {
                if (((xy.z.x - x) * (xy.z.x - x) + (xy.z.y - y) * (xy.z.y - y)) < minPixelDistSquare) {
                    nrs = nrs + 1;
                    s.isSelected = false;
                    selectedShape = s;
                };
            };
        };

        if (nrs == 0) {
            System.out.println("No shapes unselected");
            return 0;
        } else {
            System.out.println(nrs + " shapes unselected ");
            return nrs;
        }

    }

    public void selectAll() {
        for (Shape s : sList) {
            s.isSelected = true;
        };
    }

    public void unselectAll() {
        for (Shape s : sList) {
            s.isSelected = false;
        };
    }

   
    
     public void selectArea() {
// preSelect all shapes that have a point in the cursor area
// preSelection is reset each time this method is called due to cursor movement

        for (Shape s : sList) {
            s.isPreSelected=false;
            for (Point xy : s.pList) {
                if ((xy.z.x >= areaCursorX1) && (xy.z.x <= areaCursorX2)
                        && (xy.z.y >= areaCursorY1) && (xy.z.y <= areaCursorY2)) {
                    s.isPreSelected = true;
                }
            }
        }
    }
     
          public void unselectArea() {
// preUnselect all shapes that have a point in the cursor area
// preUnselection is reset each time this method is called due to cursor movement

        for (Shape s : sList) {
            s.isPreUnselected=false;
            for (Point xy : s.pList) {
                if ((xy.z.x >= areaCursorX1) && (xy.z.x <= areaCursorX2)
                        && (xy.z.y >= areaCursorY1) && (xy.z.y <= areaCursorY2)) {
                    s.isPreUnselected = true;
                }
            }
        }
    }

      public void commitSelectedArea() {
// Select all shapes that were preselected. Called when area selection is final

        for (Shape s : sList) {
            if (s.isPreSelected) {
                s.isPreSelected=false;
                s.isSelected=true;
            }
        }
    }
      
       public void commitUnselectedArea() {
// Select all shapes that were preselected. Called when area selection is final

        for (Shape s : sList) {
            if (s.isPreUnselected) {
                s.isPreUnselected=false;
                s.isSelected=false;
            }
        }
    }
} // Drawing
