package AST;

import java.util.*;

public class Assign_stmt implements Stmt {
	private Assign_expr ae;

	public Assign_stmt(Assign_expr ae){
		this.ae = ae;
	}

	public void genC(PW pw){
		ae.genC(pw);
		pw.print(";", false);
	}
}
