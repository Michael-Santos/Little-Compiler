package AST;

import java.util.*;

public class Factor {
	private Postfix_expr pos;
	private ArrayList<Factor_tail> ft;

	public Factor(Postfix_expr pos, ArrayList<Factor_tail> ft){
		this.pos = pos;
		this.ft = ft;
	}

	public void genC(PW pw) {
		pos.genC(pw);
		
		if(!ft.isEmpty()) {
			for(Factor_tail f : ft) {
				f.genC(pw);
			}
		}
  	}
}
