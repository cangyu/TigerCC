package compiler.Parser;

import java.util.*;

public class ExclusiveOrExpr extends Expr
{
	public LinkedList<Expr> expr_list;

	public ExclusiveOrExpr()
	{
		expr_list = new LinkedList<Expr>();
	}

	public void add_expr(Expr x)
	{
		expr_list.add(x);
	}
}