package AST;

import java.util.*;

public class Var_decl {
  private Identifier id;
  private String tipo;

  public Var_decl( Identifier id, String tipo ) {
    this.id = id;
    this.tipo = tipo;
  }

  public String getTipo() {
    return this.tipo;
  }

  public void genC(PW pw) {
    pw.println(tipo.toLowerCase() + " " + id.getName() + ";");
  }

  public void genC(PW pw, boolean type) {
    if(type) {
      pw.print(tipo.toLowerCase() + " " + id.getName());
    } else {
      pw.print(id.getName(), false);
    }
  }
}
