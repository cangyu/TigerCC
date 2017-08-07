package compiler.AST;

public class IterStmt extends Stmt
{
	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
