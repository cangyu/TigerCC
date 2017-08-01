package compiler.Parser;

import java.util.*;

public class RelationalExpr extends BinaryExpr
{
	public LinkedList<ShiftExpr> expr_list;

	public RelationalExpr(int _t)
	{
		super(_t);
		expr_list = new LinkedList<ShiftExpr>();
	}

	public void add_expr(ShiftExpr x)
	{
		expr_list.add(x);
	}

}