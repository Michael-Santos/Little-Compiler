package AST;

import java.util.*;

public class If_stmt implements Stmt{
	private Cond cond;
	private ArrayList<Stmt> st;
	private Else_part ep;

	public If_stmt(Cond cond, ArrayList<Stmt> st, Else_part ep){
		this.cond = cond;
		this.st = st;
		this.ep = ep;
	}

	public void genC(PW pw){
		pw.print("if (", false);

		cond.genC(pw);

		pw.println(") {", false);
		pw.add();

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
				s.genC(pw);
				pw.println("", false);
			}
		}

		pw.sub();
		pw.print("}");

		if(ep != null) {
			ep.genC(pw);
		}
	}
}
