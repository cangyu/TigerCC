package compiler.Parser;

import java.util.LinkedList;

public class LogicalAndExpr extends Expr
{
	public LinkedList<Expr> expr_list;

	public LogicalAndExpr()
	{
		expr_list = new LinkedList<Expr>();
	}

	public void add_expr(Expr x)
	{
		expr_list.add(x);
	}
}