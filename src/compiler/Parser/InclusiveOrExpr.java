package compiler.Parser;

import java.util.*;

public class InclusiveOrExpr
{
	public LinkedList<ExclusiveOrExpr> expr_list;

	public InclusiveOrExpr()
	{
		expr_list = new LinkedList<ExclusiveOrExpr>();
	}

	public void add_expr(ExclusiveOrExpr x)
	{
		expr_list.add(x);
	}
}