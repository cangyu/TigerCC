package compiler.Parser;

import java.util.*;

public class MultiplicativeExpr extends BinaryExpr
{
	public LinkedList<CastExpr> expr_list;
	public LinkedList<Integer> op_list;

	public MultiplicativeExpr()
	{
		super();
		expr_list = new LinkedList<CastExpr>();
		op_list = new LinkedList<Integer>();
	}

	public void add_expr(CastExpr x)
	{
		expr_list.add(x);
	}

	public void add_expr(CastExpr x, int op)
	{
		expr_list.add(x);
		op_list.add(op);
	}
}