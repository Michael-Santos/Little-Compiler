package AST;

import java.util.*;

public class Program {
  private Identifier id;
  private Pgm_body pb;

  public Program( Identifier id, Pgm_body pb ) {
    this.id = id;
    this.pb = pb;
  }

  public void genC(PW pw) {
    pw.println("#include <stdio.h>");
    pw.println("// Programa \"" + id.getName() + "\"\n");

    pb.genC(pw);
  }
}
