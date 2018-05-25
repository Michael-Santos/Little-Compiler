package AST;

public interface Stmt {
	public void genC(PW pw);
	public void genC(PW pw, boolean semicolon);
}