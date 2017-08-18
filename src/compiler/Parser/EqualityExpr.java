package compiler.Parser;

import java.util.*;

public class EqualityExpr extends Expr
{
	public LinkedList<Expr> expr_list;
	public LinkedList<Integer> op_list;

	public EqualityExpr()
	{
		expr_list = new LinkedList<Expr>();
		op_list = new LinkedList<Integer>();
	}

	public void add_expr(Expr x)
	{
		expr_list.add(x);
	}

	public void add_expr(Expr expr, int op)
	{
		op_list.add(op);
		expr_list.add(expr);
	}
}