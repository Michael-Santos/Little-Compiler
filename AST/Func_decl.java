package AST;

import java.util.*;

public class Func_decl implements Typable {
  private String type;
  private Identifier id;
  private ArrayList<Param_decl> pd;
  private Func_body fb;

  public Func_decl(String type, Identifier id, ArrayList<Param_decl> pd, Func_body fb) {
  	this.type = type;
  	this.id = id;
  	this.pd = pd;
  	this.fb = fb;
  }

  public Func_decl(String type, Identifier id) {
  	this.type = type;
  	this.id = id;
  }
  
  public String getType() {
    return (this.type);
  }
  
  public Identifier getId() {
    return (this.id);
  }
  
  public ArrayList<Param_decl> getPd() {
    return (this.pd);
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public void setId(Identifier id) {
    this.id = id;
  }
  
  public void setPd(ArrayList<Param_decl> pd) {
    this.pd = pd;
  }
  
  public void setFb(Func_body fb) {
    this.fb = fb;
  }
  
  public void genC(PW pw) {
    pw.print(type.toLowerCase() + " " + id.getName() + " (");

    boolean flagFirst = true;
    for (Param_decl p : pd) {
      if(flagFirst) {
        flagFirst = false;
      } else {
        pw.print(", ", false);
      }
      p.genC(pw);
    }

    pw.println(") {", false);
    pw.add();

    fb.genC(pw);

    pw.sub();
    pw.println("}");
  }
  
  // Usada para impressão de assinatura antes da main
  public void genC(PW pw, boolean sig) {
    if(sig) {
      if (!id.getName().equals("main")) {
        pw.print(type.toLowerCase() + " " + id.getName() + " (");
        
        boolean flagFirst = true;
        for (Param_decl p : pd) {
          if(flagFirst) {
            flagFirst = false;
          } else {
            pw.print(", ", false);
          }
          p.genC(pw);
        }
    
        pw.println(");", false);
      }
    } else {
      pw.print(type.toLowerCase() + " " + id.getName() + " (");
  
      boolean flagFirst = true;
      for (Param_decl p : pd) {
        if(flagFirst) {
          flagFirst = false;
        } else {
          pw.print(", ", false);
        }
        p.genC(pw);
      }
  
      pw.println(") {", false);
      pw.add();
  
      fb.genC(pw);
  
      pw.sub();
      pw.println("}");
    }
  }
}
