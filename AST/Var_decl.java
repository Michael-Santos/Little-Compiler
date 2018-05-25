package AST;

import java.util.*;

public class Var_decl implements Typable {
  private Identifier id;
  private String type;

  public Var_decl( Identifier id, String type ) {
    this.id = id;
    this.type = type;
  }

  public Identifier getId() {
    return (this.id);
  }

  public String getType() {
    return (this.type);
  }

  public void genC(PW pw) {
    pw.println(type.toLowerCase() + " " + id.getName() + ";");
  }

  public void genC(PW pw, boolean type) {
    if(type) {
      pw.print(this.type.toLowerCase() + " " + id.getName());
    } else {
      pw.print(id.getName(), false);
    }
  }
}
