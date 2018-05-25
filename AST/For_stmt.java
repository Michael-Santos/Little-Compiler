package AST;

import java.util.*;

public class For_stmt implements Stmt{
	private Assign_stmt assign_init, assign_step;
	private Cond cond;
	private ArrayList<Stmt> st;

	public For_stmt(Assign_stmt assign_init, Cond cond, Assign_stmt assign_step, ArrayList<Stmt> st){
		this.assign_init = assign_init;
		this.cond = cond;
		this.assign_step = assign_step;
		this.st = st;
	}

	public void genC(PW pw){
		pw.print("for (", false);

		assign_init.genC(pw, false);

		if(cond != null) {
			pw.print("; ", false);
			cond.genC(pw);
		} else {
			pw.print(";", false);
		}

		if(assign_step != null) {
			pw.print("; ", false);
			assign_step.genC(pw, false);
		} else {
			pw.print(";", false);
		}

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
				s.genC(pw, true);
				pw.println("", false);
			}
		}

		pw.sub();
		pw.print("}");
	}

	// For não tem semicolon sob nenhuma hipótese
	public void genC(PW pw, boolean semicolon) {
		this.genC(pw);
	}
}
