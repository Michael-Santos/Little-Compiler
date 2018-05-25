package AST;

import java.util.*;

public class Else_part {
	private ArrayList<Stmt> st;

	public Else_part(ArrayList<Stmt> st){
		this.st = st;
	}

	public void genC(PW pw){
		pw.println(" else {", false);
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
