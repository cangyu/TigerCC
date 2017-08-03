package compiler.Parser;

public class ExpressionStmt extends Statement
{
	public Expression elem;

	public ExpressionStmt(Expression x)
	{
		elem = x;
	}
}
