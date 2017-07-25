package compiler.AST;

import java.util.LinkedList;

public class Declarator extends ASTNode
{
	public PlainDeclarator plain_declarator;
	public LinkedList<Expr> dimension;

	public Declarator(PlainDeclarator _pd)
	{
		plain_declarator = _pd;
		dimension = new LinkedList<Expr>();
	}

	public void add_expr(Expr e)
	{
		dimension.add(e);
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
