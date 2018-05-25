package AST;

import java.util.*;

public class Return_stmt implements Stmt {
	private Expr expr;

	public Return_stmt(Expr expr){
		this.expr = expr;
	}

	public void genC(PW pw){
		pw.print("return(", false);

		expr.genC(pw);

		pw.print(");", false);
	}
}
