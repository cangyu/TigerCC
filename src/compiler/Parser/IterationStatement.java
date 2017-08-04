package compiler.Parser;

public class IterationStatement extends Statement
{
	public static final int WHILE = 0;
	public static final int FOR = 0;

	public int type;
	public Expression init, judge, next;
	public Statement stmt;

	public IterationStatement(Expression cond, Statement st)
	{
		type = WHILE;
		init = null;
		judge = cond;
		next = null;
		stmt = st;
	}

	public IterationStatement(Expression t1, Expression t2, Expression t3, Statement st)
	{
		type = FOR;
		init = t1;
		judge = t2;
		next = t3;
		stmt = st;
	}
}
