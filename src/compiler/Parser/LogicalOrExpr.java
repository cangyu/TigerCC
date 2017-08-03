package compiler.Parser;

import java.util.*;

public class LogicalOrExpr
{
	public LinkedList<LogicalAndExpr> expr_list;

	public LogicalOrExpr()
	{
		expr_list = new LinkedList<LogicalAndExpr>();
	}

	public void add_expr(LogicalAndExpr x)
	{
		expr_list.add(x);
	}
}
