package compiler.AST;

public class SelectStmt extends Stmt
{
	public CommaExp condition;
	public Stmt if_branch, else_branch;

	public SelectStmt(CommaExp c, Stmt st, Stmt sf)
	{
		condition = c;
		if_branch = st;
		else_branch = sf;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
