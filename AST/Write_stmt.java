package AST;

import java.util.*;

public class Write_stmt implements Stmt {
	private ArrayList<Identifier> id;

	public Write_stmt(ArrayList<Identifier> id){
		this.id = id;
	}

	public void genC(PW pw){
		pw.print("printf(", false);

		if(!id.isEmpty()) {
			boolean flagFirst = true;

			for(Identifier i : id) {
				if(flagFirst) {
				flagFirst = false;
				} else {
				pw.print(", ", false);
				}
				i.genC(pw);
			}
		}

		pw.print(");", false);
	}

	public void genC(PW pw, boolean semicolon){
		pw.print("printf(", false);

		if(!id.isEmpty()) {
			boolean flagFirst = true;

			for(Identifier i : id) {
				if(flagFirst) {
				flagFirst = false;
				} else {
				pw.print(", ", false);
				}
				i.genC(pw);
			}
		}

		pw.print(")", false);

		if (semicolon) {
			pw.print(";", false);
		}
	}
}
