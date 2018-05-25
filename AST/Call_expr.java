package AST;

import java.util.*;

public class Call_expr implements Postfix_expr {
  private Identifier id;
  private ArrayList<Expr> expr_list;

  public Call_expr( Identifier id, ArrayList<Expr> expr_list ) {
    this.id = id;
    this.expr_list = expr_list;
  }

  public void genC(PW pw) {
    pw.print(id.getName() + " (", false);

    if(!expr_list.isEmpty()) {
      boolean flagFirst = true;

      for(Expr e : expr_list) {
        if(flagFirst) {
          flagFirst = false;
        } else {
          pw.print(", ", false);
        }
        e.genC(pw);
      }
    }

    pw.print(")", false);
	}
}
