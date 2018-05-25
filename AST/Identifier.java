package AST;

import java.util.*;

public class Identifier implements Postfix_expr {
  private String name;

  public Identifier( String name ) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void genC(PW pw) {
    pw.print(name, false);
  }
}
