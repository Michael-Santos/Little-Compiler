package AST;

import java.util.*;

public class Call_stmt implements Stmt {
  private Call_expr ce;

  public Call_stmt( Call_expr ce ) {
    this.ce = ce;
  }

  public void genC(PW pw) {
    ce.genC(pw);
    pw.print(";", false);
  }
}
