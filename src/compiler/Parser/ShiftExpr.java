package compiler.Parser;

import java.util.*;

public class ShiftExpr extends BinaryExpr
{
	public LinkedList<AdditiveExpr> expr_list;
	public LinkedList<Integer> op_list;

	public ShiftExpr()
	{
		super();
		expr_list = new LinkedList<AdditiveExpr>();
		op_list = new LinkedList<Integer>();
	}

	public void add_expr(AdditiveExpr x)
	{
		expr_list.add(x);
	}

	public void add_expr(AdditiveExpr x, int op)
	{
		expr_list.add(x);
		op_list.add(op);
	}
}