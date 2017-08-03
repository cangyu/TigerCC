package compiler.Parser;

public class JumpStmt extends Statement
{
	public static final int CTNU = 0;
	public static final int BRK = 1;
	public static final int RET = 1;

	public int type;
	public Expression expr;

	public JumpStmt(int jt, Expression e)
	{
		type = jt;
		expr = e;
	}
}
