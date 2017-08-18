package compiler.Parser;

import java.util.*;

public class AdditiveExpr extends Expr
{
	public LinkedList<Expr> expr_list;
	public LinkedList<Integer> op_list;

	public AdditiveExpr()
	{
		expr_list = new LinkedList<Expr>();
		op_list = new LinkedList<Integer>();
	}

	public void add_expr(Expr x)
	{
		expr_list.add(x);
	}

	public void add_expr(Expr x, int op)
	{
		expr_list.add(x);
		op_list.add(op);
	}
}