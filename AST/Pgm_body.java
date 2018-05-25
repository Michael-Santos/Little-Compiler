package AST;

import java.util.*;

public class Pgm_body {
  private Decl dl;
  private ArrayList<Func_decl> fd;

  public Pgm_body(Decl dl, ArrayList<Func_decl> fd) {
    this.dl = dl;
    this.fd = fd;
  }

  public void genC(PW pw) {
    dl.genC(pw);

    if(!fd.isEmpty()) {
      boolean flagFirst = true;

      for (Func_decl f : fd) {
        if(flagFirst) {
					flagFirst = false;
				} else {
					pw.println("");
				}
				f.genC(pw);
      }
    }
  }
}
