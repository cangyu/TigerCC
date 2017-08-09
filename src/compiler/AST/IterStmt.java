package compiler.AST;

public class IterStmt extends Stmt
{
	public static final int iter_while = 0;
	public static final int iter_for = 1;

	public int category;
	public CommaExp init, judge, next;
	public Stmt stmt;

	public IterStmt(CommaExp e, Stmt s)
	{
		category = iter_while;
		init = null;
		judge = e;
		next = null;
		stmt = s;
	}

	public IterStmt(CommaExp e1, CommaExp e2, CommaExp e3, Stmt s)
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
