package compiler.Parser;

import java.util.*;

public class ExclusiveOrExpr extends BinaryExpr
{
	public LinkedList<AndExpr> expr_list;

	public ExclusiveOrExpr()
	{
		super(BinaryExpr.BIT_XOR);
		expr_list = new LinkedList<AndExpr>();
	}

	public void add_expr(AndExpr x)
	{
		expr_list.add(x);
	}
}