package compiler.Parser;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class PlainDeclaration extends ASTNode
{
	public TypeSpecifier ts;
	public Declarator dlr;

	public PlainDeclaration(TypeSpecifier _ts, Declarator _d)
	{
		ts = _ts;
		dlr = _d;
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
