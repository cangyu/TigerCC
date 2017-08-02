package compiler.Parser;

import java.util.*;

public class AdditiveExpr extends BinaryExpr
{
	public LinkedList<MultiplicativeExpr> expr_list;
	public LinkedList<Integer> op_list;

	public AdditiveExpr()
	{
		super();
		expr_list = new LinkedList<MultiplicativeExpr>();
		op_list = new LinkedList<Integer>();
	}

	public void add_expr(MultiplicativeExpr x)
	{
		expr_list.add(x);
	}

	public void add_expr(MultiplicativeExpr x, int op)
	{
		expr_list.add(x);
		op_list.add(op);
	}
}