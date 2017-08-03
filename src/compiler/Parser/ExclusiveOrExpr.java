package compiler.Parser;

import java.util.*;

public class ExclusiveOrExpr
{
	public LinkedList<AndExpr> expr_list;

	public ExclusiveOrExpr()
	{
		expr_list = new LinkedList<AndExpr>();
	}

	public void add_expr(AndExpr x)
	{
		expr_list.add(x);
	}
}