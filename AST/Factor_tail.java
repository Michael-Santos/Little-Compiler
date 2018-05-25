package AST;

import java.util.*;

public class Factor_tail {
	private String mulop;
	private Postfix_expr pos;

	public Factor_tail(String mulop, Postfix_expr pos){
		this.mulop = mulop;
		this.pos = pos;
	}

	public void genC(PW pw) {
		pw.print(" " + mulop + " ", false);
		pos.genC(pw);
	}
}
