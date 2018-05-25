package AST;

import java.util.*;

public class String_decl implements Typable {
  private Identifier id;
  private String st;

  public String_decl( Identifier id, String st ) {
    this.id = id;
    this.st = st;
  }
  
  public Identifier getId() {
    return (this.id);
  }

  public String getType() {
    return ("STRING");
  }
  
  public void genC(PW pw) {
    pw.println("char " + id.getName() + "[] = \"" + st + "\";");
  }
}
