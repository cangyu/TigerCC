package compiler.Parser;

public class SelectionStatement extends Statement
{
	public Expr cond;
	public Statement if_clause, else_clause;

	public SelectionStatement(Expr _cond, Statement _ic, Statement _ec)
	{
		cond = _cond;
		if_clause = _ic;
		else_clause = _ec;
	}
}
