package compiler.AST;

public class JumpStmt extends Stmt
{
	public static final int jp_ctn = 0;
	public static final int jp_brk = 1;
	public static final int jp_ret = 2;

	public int category;
	public CommaExp expr;

	public JumpStmt(int c)
	{
		category = c;
		expr = null;
	}

	public JumpStmt(CommaExp e)
	{
		category = jp_ret;
		expr = e;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
