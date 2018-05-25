package AST;

import java.util.*;

public class Decl {
  private ArrayList<String_decl> sd;
  private ArrayList<Var_decl> vd;

  public Decl(ArrayList<String_decl> sd, ArrayList<Var_decl> vd) {
    this.sd = sd;
    this.vd = vd;
  }

  public void genC(PW pw) {
    if(!sd.isEmpty()) {
      for (String_decl s : sd) {
        s.genC(pw);
      }

      pw.println("");
    }
    
    if(!vd.isEmpty()) {
      ArrayList<Var_decl> intd = new ArrayList<Var_decl>();
      ArrayList<Var_decl> floatd = new ArrayList<Var_decl>();

      for (Var_decl v : vd) {
        if(v.getType() == "INT") {
          intd.add(v);
        } else {
          floatd.add(v);
        }
      }

      boolean flagType;

      if(!intd.isEmpty()) {
        flagType = true;

        for (Var_decl v : intd) {
          if(flagType) {
            v.genC(pw, flagType);
            flagType = false;
          } else {
            pw.print(", ", false);
            v.genC(pw, flagType);
          }
        }
        pw.println(";\n", false);
      }

      if(!floatd.isEmpty()) {
        flagType = true;

        for (Var_decl v : floatd) {
          if(flagType) {
            v.genC(pw, flagType);
            flagType = false;
          } else {
            pw.print(", ", false);
            v.genC(pw, flagType);
          }
        }
        pw.println(";\n", false);
      }
    }
  }
}
