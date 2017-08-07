package compiler.AST;

public class ExprStmt extends Stmt
{
	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
