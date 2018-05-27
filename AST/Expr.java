package AST;

import java.util.*;

public class Expr implements Postfix_expr {
  private String type;
  private Factor factor;
  private ArrayList<Expr_tail> expr_tail;

  public Expr(Factor factor, ArrayList<Expr_tail> expr_tail, String type) {
    this.factor = factor;
    this.expr_tail = expr_tail;
    
    if (type == null) {
      this.type = "VOID";
    } else {
      this.type = type;
    }
  }
  
  public String getType() {
    return (this.type);
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
