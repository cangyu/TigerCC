package compiler.Parser;

import java.util.*;

public class InclusiveOrExpr extends Expr
{
	public LinkedList<Expr> expr_list;

	public InclusiveOrExpr()
	{
		expr_list = new LinkedList<Expr>();
	}

	public void add_expr(Expr x)
	{
		expr_list.add(x);
	}
}