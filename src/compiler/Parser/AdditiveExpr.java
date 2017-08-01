package compiler.Parser;

import java.util.*;

public class AdditiveExpr extends BinaryExpr
{
	public LinkedList<MultiplicativeExpr> expr_list;

	public AdditiveExpr(int _t)
	{
		super(_t);
		expr_list = new LinkedList<MultiplicativeExpr>();
	}

	public void add_expr(MultiplicativeExpr x)
	{
		expr_list.add(x);
	}
}