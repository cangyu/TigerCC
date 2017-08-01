package compiler.Parser;

import java.util.*;

public class EqualityExpr extends BinaryExpr
{
	public LinkedList<RelationalExpr> expr_list;

	public EqualityExpr(int _t)
	{
		super(_t);
		expr_list = new LinkedList<RelationalExpr>();
	}

	public void add_expr(RelationalExpr x)
	{
		expr_list.add(x);
	}
}