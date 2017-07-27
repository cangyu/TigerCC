package compiler.Parser;

import compiler.AST.ASTNodeVisitor;

public class JumpStmt extends Stmt
{
	public static final int CTNU = 0;
	public static final int BRK = 1;
	public static final int RET = 1;

	public int type;
	public Expr expr;

	public JumpStmt(int jt, Expr e)
	{
		type = jt;
		expr = e;
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
