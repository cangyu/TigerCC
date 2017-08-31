package compiler.AST;

import java.util.LinkedList;

public class CommaExp extends Exp
{
	public LinkedList<Exp> exp;

	public CommaExp()
	{
		super();
		exp = new LinkedList<Exp>();
	}

	public void add_exp(Exp x)
	{
		exp.add(x);
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
