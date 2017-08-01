package compiler.Parser;

import java.util.*;

public class InclusiveOrExpr extends BinaryExpr
{
	public LinkedList<ExclusiveOrExpr> expr_list;

	public InclusiveOrExpr()
	{
		super(BinaryExpr.BIT_OR);
		expr_list = new LinkedList<ExclusiveOrExpr>();
	}

	public void add_expr(ExclusiveOrExpr x)
	{
		expr_list.add(x);
	}
}