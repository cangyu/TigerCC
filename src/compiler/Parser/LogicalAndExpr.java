package compiler.Parser;

import java.util.LinkedList;

public class LogicalAndExpr extends BinaryExpr
{
	public LinkedList<InclusiveOrExpr> expr_list;

	public LogicalAndExpr()
	{
		super(BinaryExpr.AND);
		expr_list = new LinkedList<InclusiveOrExpr>();
	}

	public void add_expr(InclusiveOrExpr x)
	{
		expr_list.add(x);
	}
}