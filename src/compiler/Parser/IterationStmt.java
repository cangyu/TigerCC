package compiler.Parser;

import compiler.AST.ASTNodeVisitor;

public class IterationStmt extends Stmt
{
	public static final int WHILE = 0;
	public static final int FOR = 0;

	public int type;
	public Expr init, judge, next;
	public Stmt stmt;

	public IterationStmt(Expr cond, Stmt st)
	{
		type = WHILE;
		init = null;
		judge = cond;
		next = null;
		stmt = st;
	}

	public IterationStmt(Expr t1, Expr t2, Expr t3, Stmt st)
	{
		type = FOR;
		init = t1;
		judge = t2;
		next = t3;
		stmt = st;
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
