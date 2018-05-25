package AST;

import java.util.*;

public class Param_decl implements Typable {
  private String type;
  private Identifier id;

  public Param_decl( String type, Identifier id ) {
    this.type = type;
    this.id = id;
  }
  
  public Identifier getId() {
    return (this.id);
  }

  public String getType() {
    return (this.type);
  }
  
  public void genC(PW pw) {
    pw.print(type.toLowerCase() + " " + id.getName(), false);
  }
}
