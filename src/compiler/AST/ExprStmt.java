package compiler.AST;

public class ExprStmt extends Stmt
{
	public Exp exp;

	public ExprStmt(Exp ce)
	{
		exp = ce;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
