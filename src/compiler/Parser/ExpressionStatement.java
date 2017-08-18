package compiler.Parser;

public class ExpressionStatement extends Statement
{
	public Expr elem;

	public ExpressionStatement(Expr x)
	{
		elem = x;
	}
}
