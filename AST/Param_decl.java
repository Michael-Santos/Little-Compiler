package AST;

import java.util.*;

public class Param_decl {
  private String tipo;
  private Identifier id;

  public Param_decl( String tipo, Identifier id ) {
    this.tipo = tipo;
    this.id = id;
  }

  public void genC(PW pw) {
    pw.print(tipo.toLowerCase() + " " + id.getName(), false);
  }
}
