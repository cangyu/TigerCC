package compiler.AST;

public class IterStmt extends Stmt
{
	public static final int iter_while = 0;
	public static final int iter_for = 1;

	public int category;
	public Exp init, judge, next;
	public Stmt stmt;

	public IterStmt(Exp e, Stmt s)
	{
		category = iter_while;
		init = null;
		judge = e;
		next = null;
		stmt = s;
	}

	public IterStmt(Exp e1, Exp e2, Exp e3, Stmt s)
	{
		category = iter_for;
		init = e1;
		judge = e2;
		next = e3;
		stmt = s;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
