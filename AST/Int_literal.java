package AST;

import java.util.*;

public class Int_literal implements Postfix_expr {
  private int value;

  public Int_literal( int value ) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public void genC(PW pw) {
    pw.print(String.valueOf(value), false);
  }
}
