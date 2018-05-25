package AST;

import java.util.*;

public class Func_decl {
  private String tipo;
  private Identifier id;
  private ArrayList<Param_decl> pd;
  private Func_body fb;

  public Func_decl(String tipo, Identifier id, ArrayList<Param_decl> pd, Func_body fb) {
  	this.tipo = tipo;
  	this.id = id;
  	this.pd = pd;
  	this.fb = fb;
  }

  public void genC(PW pw) {
    pw.print(tipo.toLowerCase() + " " + id.getName() + " (");

    boolean flagFirst = true;
    for (Param_decl p : pd) {
      if(flagFirst) {
        flagFirst = false;
      } else {
        pw.print(", ", false);
      }
      p.genC(pw);
    }

    pw.println(") {", false);
    pw.add();

    fb.genC(pw);

    pw.sub();
    pw.println("}");
  }
}
