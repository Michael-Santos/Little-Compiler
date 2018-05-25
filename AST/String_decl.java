package AST;

import java.util.*;

public class String_decl {
  private Identifier id;
  private String st;

  public String_decl( Identifier id, String st ) {
    this.id = id;
    this.st = st;
  }

  public void genC(PW pw) {
    pw.println("char[] " + id.getName() + " = \"" + st + "\";");
  }
}
