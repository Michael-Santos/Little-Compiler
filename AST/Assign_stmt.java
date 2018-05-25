package AST;

import java.util.*;

public class Assign_stmt implements Stmt {
	private Identifier id;
	private Expr expr;

	public Assign_stmt(Identifier id, Expr expr){
		this.id = id;
		this.expr = expr;
	}

	public void genC(PW pw){
		pw.print(id.getName() + " = ", false);
		expr.genC(pw);
		pw.print(";", false);
	}
}
