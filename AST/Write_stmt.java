package AST;

import java.util.*;

public class Write_stmt implements Stmt {
	private ArrayList<Typable> typable;

	public Write_stmt(ArrayList<Typable> typable){
		this.typable = typable;
	}

	public void genC(PW pw){
		pw.print("printf(", false);

		if(!typable.isEmpty()) {
			boolean flagFirst = true;
			
			pw.print("\"", false);

			for(Typable i : typable) {
				if(flagFirst) {
					flagFirst = false;
				} else {
					pw.print(" ", false);
				}
				
				String t = i.getType();
				
				if(t.equals("INT")) {
					pw.print("%d", false);
				} else if(t.equals("FLOAT")) {
					pw.print("%f", false);
				} else if(t.equals("STRING")) {
					pw.print("%s", false);
				}
			}
			
			pw.print("\"", false);
			
			for(Typable i : typable) {
				pw.print(", ", false);
				i.getId().genC(pw);
			}
		}

		pw.print(");", false);
	}
}
