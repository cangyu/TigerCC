package compiler.Parser;

import java.util.LinkedList;

public class LogicalAndExpr
{
	public LinkedList<InclusiveOrExpr> expr_list;

	public LogicalAndExpr()
	{
		expr_list = new LinkedList<InclusiveOrExpr>();
	}

	public void add_expr(InclusiveOrExpr x)
	{
		expr_list.add(x);
	}
}