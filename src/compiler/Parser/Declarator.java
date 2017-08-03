package compiler.Parser;

import java.util.LinkedList;

public class Declarator
{
	public PlainDeclarator plain_declarator;
	public LinkedList<ConstantExpr> dimension;

	public Declarator(PlainDeclarator _pd)
	{
		plain_declarator = _pd;
		dimension = new LinkedList<ConstantExpr>();
	}

	public void add_expr(ConstantExpr e)
	{
		dimension.add(e);
	}
}
