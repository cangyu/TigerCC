package compiler.AST;

public class AssignExp extends Exp
{
	public BinaryExp be;
	public int assign_type;
	public UnaryExp ue;
	public AssignExp ae;

	public AssignExp(BinaryExp exp)
	{
		super();
		be = exp;
		assign_type = -1;
		ue = null;
		ae = null;
	}

	public AssignExp(UnaryExp lexp, int op, AssignExp rexp)
	{
		super();
		be = null;
		ue = lexp;
		assign_type = op;
		ae = rexp;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
