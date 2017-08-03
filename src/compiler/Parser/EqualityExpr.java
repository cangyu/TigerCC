package compiler.Parser;

import java.util.*;

public class EqualityExpr
{
	public LinkedList<Integer> op_list;
	public LinkedList<RelationalExpr> expr_list;

	public EqualityExpr()
	{
		op_list = new LinkedList<Integer>();
		expr_list = new LinkedList<RelationalExpr>();
	}

	public void add_expr(RelationalExpr x)
	{
		expr_list.add(x);
	}

	public void add_expr(RelationalExpr expr, int op)
	{
		op_list.add(op);
		expr_list.add(expr);
	}
}