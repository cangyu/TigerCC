package compiler.Parser;

import compiler.AST.ASTNodeVisitor;

public class ExpressionStmt extends Stmt
{
	public Expression elem;

	public ExpressionStmt(Expression x)
	{
		elem = x;
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
