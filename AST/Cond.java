package AST;

import java.util.*;

public class Cond{
	private Expr exprDir;
	private String compop;
	private Expr exprEsq;

	public Cond(Expr exprDir, String compop, Expr exprEsq){
		this.exprDir = exprDir;
		this.compop = compop;
		this.exprEsq = exprEsq;
	}

	public void genC(PW pw){
		exprDir.genC(pw);
		
		if (compop.equals("=")){
			pw.print(" == ", false);
		}else{
			pw.print(" " + compop + " ", false);
		}
		
		exprEsq.genC(pw);
	}
}
