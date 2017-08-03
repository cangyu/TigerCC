package compiler.Parser;

import java.util.*;

public class AndExpr
{
	public LinkedList<EqualityExpr> expr_list;

	public AndExpr()
	{
		expr_list = new LinkedList<EqualityExpr>();
	}

	public void add_expr(EqualityExpr x)
	{
		expr_list.add(x);
	}

}