package compiler.Parser;

import java.util.*;

public class RelationalExpr
{
	public LinkedList<ShiftExpr> expr_list;
	public LinkedList<Integer> op_list;

	public RelationalExpr()
	{
		expr_list = new LinkedList<ShiftExpr>();
		op_list = new LinkedList<Integer>();
	}

	public void add_expr(ShiftExpr x)
	{
		expr_list.add(x);
	}

	public void add_expr(ShiftExpr x, int op)
	{
		expr_list.add(x);
		op_list.add(op);
	}

}