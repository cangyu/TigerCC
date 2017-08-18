package compiler.Parser;

import java.util.*;

public class Expression extends Expr
{
	public LinkedList<Expr> elem;

	public Expression()
	{
		elem = new LinkedList<Expr>();
	}

	public void add_expr(Expr x)
	{
		elem.add(x);
	}
}
