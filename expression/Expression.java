package expression;

import java.util.*;
import java.io.Console;

 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;




public class Expression {

public static final int EXPR_Z=0;
public static final int EXPR_FUNCTION=1;
public static final int EXPR_VARIABLE=2;
public static final int EXPR_OPERATION=3;
public static final int EXPR_REALCONSTANT=4;
public static final int EXPR_IMAGINARYCONSTANT=5;

public ExpressionRoot root;
public int type;
public String name;  // name of operator, variable or function
public Expression subexpr1,subexpr2;
public Complex value;  // if the node is a variable, real or imaginary constant

public Expression(){  // dummy Expression needed to call readExpr method
}

public Expression(int t,String n, Expression e1, Expression e2){
 type=t;
 name=n;
 subexpr1=e1;
 subexpr2=e2;
}

public static boolean isFunction(String name) {
if (name.equals("sin")) return true;
if (name.equals("cos")) return true;
if (name.equals("ln")) return true;
if (name.equals("conj")) return true;
if (name.equals("mod")) return true;
return false;
}

public static boolean isVariableName(String name) {
return name.matches("[a-zA-Z][a-zA-Z0-9]*");
}

public static boolean isRealConstant(String name) {
return name.matches("[0-9]+[.]?[0-9]*");
}

public static boolean isImaginaryConstant(String name) {
return ( name.equals("i") || name.matches("i[0-9]+[.]?[0-9]*") || name.matches("[0-9]+[.]?[0-9]*i") );
}



public static void printExpr(Expression e){
if (e==null) {
 System.out.printf("null");
} else if (e.type==EXPR_FUNCTION) {
 System.out.printf(e.name);
 System.out.printf("(");
 printExpr(e.subexpr1);
 System.out.printf(")");
} else if (e.type==EXPR_VARIABLE) {
 System.out.printf(e.name);
} else if (e.type==EXPR_Z) {
 System.out.printf(e.name);
} else if (e.type==EXPR_REALCONSTANT) {
 System.out.printf(e.name);
} else if (e.type==EXPR_IMAGINARYCONSTANT) {
 System.out.printf("%si",e.name);
} else if (e.type==EXPR_OPERATION) {
 System.out.printf("(");
 printExpr(e.subexpr1);
 System.out.printf(")");
 System.out.printf(e.name);
 System.out.printf("(");
 printExpr(e.subexpr2);
 System.out.printf(")");
};

}


public static boolean validateExpr(Expression e){
if (e==null) {
 return false;
} else if (e.type==EXPR_FUNCTION) {
 return validateExpr(e.subexpr1);
} else if (e.type==EXPR_VARIABLE) {
 return true;
} else if (e.type==EXPR_Z) {
 return true; 
} else if (e.type==EXPR_REALCONSTANT) {
 return true;
} else if (e.type==EXPR_IMAGINARYCONSTANT) {
 return true;
} else if (e.type==EXPR_OPERATION) {
 if (! validateExpr(e.subexpr1)) return false;
 if (! validateExpr(e.subexpr2)) return false;
 return true;
} else { // not a possible case 
 return false;
}

}






public static Expression readExpr(TokenList t,int level) {

//for (int i=0; i<=level; i++) {System.out.printf(" ");};
//System.out.println("readExpr level "+level+" next token = "+t.get());

return readTerms(t,level+1);

} // readExpr


public static Expression readTerms(TokenList t,int level){

Expression exprTerm1,exprTerm2=null;

//for (int i=0; i<=level; i++) {System.out.printf(" ");};
//System.out.println("readTerms level "+level+" next token = "+t.get());

exprTerm1=readFactors(t,level+1);
if ( t.get().equals("+") || t.get().equals("-") ) {
 String operation=t.get();
 t.skip();
 exprTerm2=readTerms(t,level);
 return new Expression(Expression.EXPR_OPERATION, operation, exprTerm1, exprTerm2);
} else { 
 return exprTerm1;
}

} // readTerms



public static Expression readFactors(TokenList t,int level){

Expression exprFactor1,exprFactor2=null;

//for (int i=0; i<=level; i++) {System.out.printf(" ");};
//System.out.println("readFactors level "+level+" next token = "+t.get());

exprFactor1=readFactor(t,level+1);
if ( t.get().equals("*") || t.get().equals("/") ) {
 String operation=t.get();
 t.skip();
 exprFactor2=readFactors(t,level);
 return new Expression(Expression.EXPR_OPERATION, operation, exprFactor1, exprFactor2);
} else { 
 return exprFactor1;
}

} // readFactors



public static Expression readFactor(TokenList t,int level){

Expression exprBase,exprExponent=null;

//for (int i=0; i<=level; i++) {System.out.printf(" ");};
//System.out.println("readFactor level "+level+" next token = "+t.get());

exprBase=readTerminal(t,level+1);
if ( t.get().equals("^") ) {
 t.skip();
 exprExponent=readTerminal(t,level);
 return new Expression(Expression.EXPR_OPERATION, "^" , exprBase, exprExponent);
} else { 
 return exprBase;
}

} // readFactor




public static Expression readTerminal(TokenList t, int level){

//for (int i=0; i<=level; i++) {System.out.printf(" ");};
//System.out.println("readTerminal level "+level+" next token = "+t.get());

 if ( t.get().equals("(") ) {

  t.skip();
  Expression exprFactor= Expression.readExpr(t,level+1);

  if ( ! t.get().equals(")") ) {
   System.out.println(") expected, found "+t.get());
   return null;
  } else {
   t.skip();
   return exprFactor;
  }

} else if ( t.get().equals("-") ) { // unary operator, treated as a function called "-"
  String functionName=t.get();
  t.skip();

  Expression exprArgument= readTerminal(t,level+1);
  return new Expression(Expression.EXPR_FUNCTION, functionName, exprArgument, null);
  
     
 } else if ( isFunction(t.get() ) ) {

  String functionName=t.get();
  t.skip();

  if ( ! t.get().equals("(") ) {
   System.out.println("( expected, found "+t.get());
   return null;
  } else {
   Expression exprArgument= readTerminal(t,level+1);
   return new Expression(Expression.EXPR_FUNCTION, functionName, exprArgument, null);
  }
  

 } else if ( isRealConstant(t.get()) ) {

  String constantName=t.get();
  t.skip();
  return new Expression(Expression.EXPR_REALCONSTANT, constantName, null, null);  

 } else if ( isImaginaryConstant(t.get()) ) {

  String constantName=t.get();
  t.skip();
  if (constantName.equals("i")) {
   return new Expression(Expression.EXPR_IMAGINARYCONSTANT, "1", null, null);
  } else { 
   return new Expression(Expression.EXPR_IMAGINARYCONSTANT, constantName.replaceAll("i",""), null, null);
  }  

 } else if ( t.get().equals("z") ) {

  t.skip();
  return new Expression(Expression.EXPR_Z, "z", null, null);  

 } else if ( isVariableName(t.get()) ) {

  String variableName=t.get();
  t.skip();
  return new Expression(Expression.EXPR_VARIABLE, variableName, null, null);  

 } else {

  System.out.println("Function, variable or constant expected, found "+t.get());
  return null;

 }

} // readTerminal


} // class Expression


