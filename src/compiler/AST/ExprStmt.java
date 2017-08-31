package compiler.AST;

public class ExprStmt extends Stmt
{
	public Exp expr;

	public ExprStmt(Exp ce)
	{
		expr = ce;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
