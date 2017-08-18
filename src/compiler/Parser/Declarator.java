package compiler.Parser;

import java.util.LinkedList;

public class Declarator
{
	public PlainDeclarator plain_declarator;
	public LinkedList<Expr> dimension; // here the expr should be constant

	public Declarator(PlainDeclarator _pd)
	{
		plain_declarator = _pd;
		dimension = new LinkedList<Expr>();
	}

	public void add_expr(Expr e)
	{
		dimension.add(e);
	}
}
