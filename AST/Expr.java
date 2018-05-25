package AST;

import java.util.*;

public class Expr implements Postfix_expr {
  private Factor factor;
  private ArrayList<Expr_tail> expr_tail;

  public Expr(Factor factor, ArrayList<Expr_tail> expr_tail) {
    this.factor = factor;
    this.expr_tail = expr_tail;
  }

  public void genC(PW pw) {
    factor.genC(pw);
    
    if(!expr_tail.isEmpty()) {
      for(Expr_tail e : expr_tail) {
        e.genC(pw);
      }
    }
  }
}
