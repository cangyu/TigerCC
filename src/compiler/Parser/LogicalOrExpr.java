package compiler.Parser;

import java.util.*;

public class LogicalOrExpr extends Expr
{
	public LinkedList<Expr> expr_list;

	public LogicalOrExpr()
	{
		expr_list = new LinkedList<Expr>();
	}

	public void add_expr(Expr x)
	{
		expr_list.add(x);
	}
}
