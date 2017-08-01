package compiler.Parser;

import java.util.*;

public class ShiftExpr extends BinaryExpr
{
	public LinkedList<AdditiveExpr> expr_list;
	
	public ShiftExpr(int _t)
	{
		super(_t);
		expr_list = new LinkedList<AdditiveExpr>();
	}

	public void add_expr(AdditiveExpr x)
	{
		expr_list.add(x);
	}
}