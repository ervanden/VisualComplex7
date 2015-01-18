package expression;

import java.util.*;



public class ExpressionRoot { // constructor parses the string and creates the Expression e

public String expressionString;
TokenList t = new TokenList();
Complex z = new Complex(0,0);
public Expression expression;


public ExpressionRoot(String s) {

 expressionString = s;
 t=TokenList.tokenize(s);
 
/*
 t.index=0;
 while ( t.get() != null ) {
  System.out.println(t.get());
  t.skip();
 };
*/
 
 // convert token list to expression tree

  t.index=0;
  expression=Expression.readExpr(t,0);

 System.out.println();
 Expression.printExpr(expression);
 System.out.println();
}




} // ExpressionRoot
