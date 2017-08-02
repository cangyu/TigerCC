package compiler.Parser;

import java.util.*;

public class RelationalExpr extends BinaryExpr
{
	public LinkedList<ShiftExpr> expr_list;
	public LinkedList<Integer> op_list;

	public RelationalExpr()
	{
		super();
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