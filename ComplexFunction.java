import java.util.*;
import java.lang.Math.*;

import expression.*;

class ComplexFunction {

    ExpressionRoot root = null;
    boolean doNotEvaluate = false;
    int nrparameters;

    public boolean setExpression(String exprString) {  // returns true if successful, false if not

        root = new ExpressionRoot(exprString); // constructor parses and builds the expression

        if (!Expression.validateExpr(root.expression)) {
            System.out.println(" Invalid expression !!");
            root = null;
            return false;
        } else {
            nrparameters = 0;
            linkExpression(root.expression, root);
            System.out.println(" number of parameters found in this expression: " + nrparameters);
            return true;

        }

    } //setExpression

    private void linkExpression(Expression e, ExpressionRoot root) {

// Every variable in the expresssion is linked to a complex number via its field 'value'.
// If a point shape exists with the name of the variable, the complex number in this point shape is used
// Otherwise a new point shape is created to represent this variable.
// For Real and Imaginary constants, Complex numbers are created for the field 'value'
        if (e != null) {

            if (e.type == Expression.EXPR_FUNCTION) {
                linkExpression(e.subexpr1, root);
            } else if (e.type == Expression.EXPR_VARIABLE) {
                // pi and e are recognized 
                if (e.name.equals("pi")) {
                    e.type = Expression.EXPR_REALCONSTANT;
                    e.value = new Complex(java.lang.Math.PI, 0);
                } else if (e.name.equals("e")) {
                    e.type = Expression.EXPR_REALCONSTANT;
                    e.value = new Complex(java.lang.Math.E, 0);
                } else {
                    Point p;
                    p = VisualComplex.Drawing.findComplexPointByName(e.name);
                    if (p == null) {
                        doNotEvaluate = true;
                        PointShape ps = VisualComplex.Drawing.addParameter(e.name);
                        nrparameters++;
                        ps.addPoint(nrparameters, nrparameters);
                        p = ps.getPoint();
                        doNotEvaluate = false;
                    };
                    e.value = p.getZ();
                }
            } else if (e.type == Expression.EXPR_REALCONSTANT) {
                e.value = new Complex(Double.parseDouble(e.name), 0);
            } else if (e.type == Expression.EXPR_IMAGINARYCONSTANT) {
                e.value = new Complex(0, Double.parseDouble(e.name));
            } else if (e.type == Expression.EXPR_OPERATION) {
                linkExpression(e.subexpr1, root);
                linkExpression(e.subexpr2, root);
            }
        }

    } // linkExpression

    public Complex evaluateExpression(Complex z) {

        if (doNotEvaluate) {
            return null;
        } else if (root == null) { // user entered an invalid expression
            return (new Complex(0, 0));
        } else {
            return eval(root.expression, z);
        }
    } //evaluateExpression

    private Complex eval(Expression e, Complex z) {

        if (e == null) {
            return null;
        } else if (e.type == Expression.EXPR_FUNCTION) {

            if (e.name.equals("-")) {
                Complex a = eval(e.subexpr1, z);
                return (new Complex(-a.x, -a.y));

            } else if (e.name.equals("conj")) {
                Complex a = eval(e.subexpr1, z);
                return (new Complex(a.x, -a.y));

            } else if (e.name.equals("mod")) {
                Complex a = eval(e.subexpr1, z);
                return (new Complex(Math.sqrt(a.x * a.x + a.y * a.y), 0.0));

            } else if (e.name.equals("sin")) {
                Complex a = eval(e.subexpr1, z);
                double cosx = Math.cos(a.x);
                double sinx = Math.sin(a.x);
                double expy = Math.exp(a.y);
                double x = (expy + 1 / expy) * sinx;
                double y = (expy - (1 / expy)) * cosx;
                return (new Complex(x / 2, y / 2));

            } else if (e.name.equals("cos")) {
                Complex a = eval(e.subexpr1, z);
                double cosx = Math.cos(a.x);
                double sinx = Math.sin(a.x);
                double expy = Math.exp(a.y);
                double x = (expy + 1 / expy) * cosx;
                double y = ((1 / expy) - expy) * sinx;
                return (new Complex(x / 2, y / 2));

            } else if (e.name.equals("ln")) {
                Complex a = eval(e.subexpr1, z);
                double x = a.x;
                double y = a.y;
                double teta = Math.atan2(y, x);
                double r = Math.sqrt(x * x + y * y);
                r = Math.log(r);
                return (new Complex(r, teta));

            } else {
                System.out.println(" Complex evaluation: function not implemented : " + e.name);
            }

        } else if (e.type == Expression.EXPR_VARIABLE) {

            return e.value;

        } else if (e.type == Expression.EXPR_Z) {

            return z;

        } else if (e.type == Expression.EXPR_REALCONSTANT) {

            return e.value;

        } else if (e.type == Expression.EXPR_IMAGINARYCONSTANT) {

            return e.value;

        } else if (e.type == Expression.EXPR_OPERATION) {

            if (e.name.equals("+")) {
                return eval(e.subexpr1, z).add(eval(e.subexpr2, z));
            } else if (e.name.equals("-")) {
                return eval(e.subexpr1, z).minus(eval(e.subexpr2, z));
            } else if (e.name.equals("*")) {
                return eval(e.subexpr1, z).mult(eval(e.subexpr2, z));
            } else if (e.name.equals("/")) {
                return eval(e.subexpr2, z).inv().mult(eval(e.subexpr1, z));
            } else if (e.name.equals("^")) {
                Complex a = eval(e.subexpr1, z);
                Complex b = eval(e.subexpr2, z);

                if (e.subexpr1.name.equals("e")) {

                    // e to complex power
                    double cosy = Math.cos(b.y);
                    double siny = Math.sin(b.y);
                    double expx = Math.exp(b.x);
                    return (new Complex(expx * cosy, expx * siny));

                } else if (Math.abs(b.y) < 1e-10) {  // exponent considered to be real

                    // complex number to real power
                    double x = a.x;
                    double y = a.y;
                    double teta = Math.atan2(y, x);
                    double r = Math.sqrt(x * x + y * y);
                    r = Math.pow(r, b.x);
                    return (new Complex(r * Math.cos(teta * b.x), r * Math.sin(teta * b.x)));

                } else {
                    System.out.println(" Complex evaluation : can only raise 'e' to complex power, or complex number to real power");
                    return (new Complex(0, 0));  // wrong but need to return something
                }
            } else {
                System.out.println(" Complex evaluation : operator not implemented : " + e.name);
            }

        };

        return null; // not a possible case

    } // evaluateExpression

}
