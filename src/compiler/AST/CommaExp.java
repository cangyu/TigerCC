package compiler.AST;

import java.util.LinkedList;

public class CommaExp extends Exp
{
	public LinkedList<AssignExp> exp;

	public CommaExp()
	{
		super();
		exp = new LinkedList<AssignExp>();
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
