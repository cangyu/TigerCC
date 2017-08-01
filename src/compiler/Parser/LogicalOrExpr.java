package compiler.Parser;

import java.util.*;

public class LogicalOrExpr extends BinaryExpr
{
	public LinkedList<LogicalAndExpr> expr_list;

	public LogicalOrExpr()
	{
		super(BinaryExpr.OR);
		expr_list = new LinkedList<LogicalAndExpr>();
	}

	public void add_expr(LogicalAndExpr x)
	{
		expr_list.add(x);
	}
}
