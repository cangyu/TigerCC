package compiler.Parser;

import compiler.AST.ASTNodeVisitor;

public class ExpressionStmt extends Stmt
{
	public Expression e;
	
	public ExpressionStmt(Expression _e)
	{
		e = _e;
	}
	
    public void accept(ASTNodeVisitor v) throws Exception
    {
        v.visit(this);
    }
}
