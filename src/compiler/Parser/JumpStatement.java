package compiler.Parser;

public class JumpStatement extends Statement
{
	public static final int CTNU = 0;
	public static final int BRK = 1;
	public static final int RET = 2;

	public int type;
	public Expr expr;

	public JumpStatement(int jt)
	{
		type = jt;
		expr = null;
	}

	public JumpStatement(Expr e)
	{
		type = RET;
		expr = e;
	}
}
