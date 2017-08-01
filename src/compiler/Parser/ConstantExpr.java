package compiler.Parser;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class ConstantExpr extends ASTNode
{
	public BinaryExpr expr;

	public ConstantExpr(BinaryExpr x)
	{
		expr = x;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}

}
