package compiler.AST;

import java.util.LinkedList;
import compiler.Typing.Type;

public class CastExp extends Exp
{
	public Exp exp;
	public LinkedList<Type> tp_seq; // here the order is reversed

	public CastExp(Exp e)
	{
		super();
		exp = e;
		tp_seq = new LinkedList<Type>();
	}

	public void add_type(Type t)
	{
		tp_seq.add(t);
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
