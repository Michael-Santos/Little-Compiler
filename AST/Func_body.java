package AST;

import java.util.*;

public class Func_body {
  private Decl dl;
  private ArrayList<Stmt> st;

  public Func_body(Decl dl, ArrayList<Stmt> st) {
  	this.dl = dl;
    this.st = st;
  }

  public void genC(PW pw) {
    dl.genC(pw);

    if(!st.isEmpty()) {
      boolean flagFirst = true;

			for(Stmt s : st) {
				if(flagFirst) {
          flagFirst = false;
          pw.print("");
				} else {
          pw.println("");
          pw.print("");
        }
        s.genC(pw, true);
        pw.println("", false);
			}
    }
  }
}
