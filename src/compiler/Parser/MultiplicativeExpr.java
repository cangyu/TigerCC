package compiler.Parser;

import java.util.*;

public class MultiplicativeExpr extends BinaryExpr
{
	public LinkedList<CastExpr> expr_list;

	public MultiplicativeExpr(int _t)
	{
		super(_t);
		expr_list = new LinkedList<CastExpr>();
	}

	public void add_expr(CastExpr x)
	{
		expr_list.add(x);
	}
}