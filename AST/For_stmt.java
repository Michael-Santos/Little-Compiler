package AST;

import java.util.*;

public class For_stmt implements Stmt{
	private Assign_expr assign_init, assign_step;
	private Cond cond;
	private ArrayList<Stmt> st;

	public For_stmt(Assign_expr assign_init, Cond cond, Assign_expr assign_step, ArrayList<Stmt> st){
		this.assign_init = assign_init;
		this.cond = cond;
		this.assign_step = assign_step;
		this.st = st;
	}

	public void genC(PW pw){
		pw.print("for (", false);

		assign_init.genC(pw);

		if(cond != null) {
			pw.print("; ", false);
			cond.genC(pw);
		} else {
			pw.print(";", false);
		}

		if(assign_step != null) {
			pw.print("; ", false);
			assign_step.genC(pw);
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
				s.genC(pw);
				pw.println("", false);
			}
		}

		pw.sub();
		pw.print("}");
	}
}
