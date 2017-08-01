package compiler.Parser;

import java.util.*;

public class AndExpr extends BinaryExpr
{
	public LinkedList<EqualityExpr> expr_list;

	public AndExpr()
	{
		super(BinaryExpr.BIT_AND);
		expr_list = new LinkedList<EqualityExpr>();
	}

	public void add_expr(EqualityExpr x)
	{
		expr_list.add(x);
	}

}