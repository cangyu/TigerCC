package compiler.AST;

public class SelectStmt extends Stmt
{
	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
