package compiler.Parser;

public class SelectionStmt extends Statement
{
	public Expression cond;
	public Statement if_clause, else_clause;

	public SelectionStmt(Expression _cond, Statement _ic, Statement _ec)
	{
		cond = _cond;
		if_clause = _ic;
		else_clause = _ec;
	}
}
