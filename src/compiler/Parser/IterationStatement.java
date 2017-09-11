package compiler.Parser;

public class IterationStatement extends Statement
{
	public static final int WHILE = 0;
	public static final int FOR = 1;

	public int type;
	public Expr init, judge, next;
	public Statement stmt;

	public IterationStatement(Expr cond, Statement st)
	{
		type = WHILE;
		init = null;
		judge = cond;
		next = null;
		stmt = st;
	}

	public IterationStatement(Expr t1, Expr t2, Expr t3, Statement st)
	{
		type = FOR;
		init = t1;
		judge = t2;
		next = t3;
		stmt = st;
	}
}
