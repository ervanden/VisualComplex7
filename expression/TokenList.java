package expression;

import java.util.*;




public class TokenList {
java.util.List<String> l = new ArrayList<String>();
public int index=0;

public String get(){ if (index>=l.size()) {return null;} else {return l.get(index);} }
public void skip(){ index++; }
public void reset(){ l.clear(); index=0; }
public void add(String name){ l.add(name); }


public static TokenList tokenize(String s) {
TokenList t=new TokenList();

t.reset();
String tokenString=s;
tokenString=tokenString.replaceAll("\\("," ( ");
tokenString=tokenString.replaceAll("\\)"," ) ");
tokenString=tokenString.replaceAll("\\+"," + ");
tokenString=tokenString.replaceAll("\\-"," - ");
tokenString=tokenString.replaceAll("\\*"," * ");
tokenString=tokenString.replaceAll("\\/"," / ");
tokenString=tokenString.replaceAll("\\^"," ^ ");
tokenString=tokenString.replaceAll("\\["," ( ");
tokenString=tokenString.replaceAll("\\]"," ) ");
// System.out.println(tokenString);
String[] tokens = tokenString.split(" ");
for (String token : tokens) {
 if (token.length()!=0) {
  // System.out.println("  |"+token+"| length="+token.length());
  t.add(token);
 };
} ;
t.add("END OF EXPRESSION"); // encountered by readTerms and readFactors
return t;
} // tokenize

} // class TokenList