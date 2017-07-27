package compiler.Parser;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class ArgumentList extends ASTNode
{
	public Expr head;
	public ArgumentList next;

	public ArgumentList(Expr _e, ArgumentList _n)
	{
		head = _e;
		next = _n;
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
