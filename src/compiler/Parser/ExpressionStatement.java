package compiler.Parser;

public class ExpressionStatement extends Statement
{
	public Expression elem;

	public ExpressionStatement(Expression x)
	{
		elem = x;
	}
}
