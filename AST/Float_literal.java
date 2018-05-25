package AST;

import java.util.*;

public class Float_literal implements Postfix_expr  {
  private float value;

  public Float_literal( float value ) {
    this.value = value;
  }

  public float getValue() {
    return this.value;
  }

  public void genC(PW pw) {
    pw.print(String.valueOf(value), false);
  }
}
