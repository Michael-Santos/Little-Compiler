package AST;

import java.util.*;

public class Expr_tail {
  private String addop;
  private Factor factor;

  public Expr_tail(String addop, Factor factor){
    this.addop = addop;
    this.factor = factor;
  }

  public void genC(PW pw) {
    pw.print(" " + addop + " ", false);
    factor.genC(pw);
  }
}
