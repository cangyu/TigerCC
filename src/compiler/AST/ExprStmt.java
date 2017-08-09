package compiler.AST;

public class ExprStmt extends Stmt
{
	public CommaExp expr;

	public ExprStmt(CommaExp ce)
	{
		expr = ce;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
