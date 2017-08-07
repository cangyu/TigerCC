package compiler.AST;

public class JumpStmt extends Stmt
{
	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
