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
      // Impressão de assinaturas
      for (Func_decl f : fd) {
				f.genC(pw, true);
      }
      pw.println("");
      
      // Impressão dos corpos das funções
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
