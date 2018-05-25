package AST;

import java.util.*;

public class Read_stmt implements Stmt {
	private ArrayList<Identifier> id;

	public Read_stmt(ArrayList<Identifier> id){
		this.id = id;
	}

	public void genC(PW pw){
		pw.print("scanf(", false);

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
		pw.print("scanf(", false);

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
