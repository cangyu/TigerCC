package compiler.Parser;

import java.util.LinkedList;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class Declarator extends ASTNode
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

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
