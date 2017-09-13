package compiler.IR;

import java.util.*;

public class Call extends Quad
{
	public LinkedList<Operand> param;

	public Call(Label name)
	{
		super(-1, null, null, name);
		param = new LinkedList<Operand>();
	}

	public void add_param(Operand x)
	{
		param.add(x);
	}

	@Override
	public String toString()
	{
		String ret = "call " + result.toString();

		Iterator<Operand> lit = param.iterator();
		while (lit.hasNext())
			ret += " " + lit.next().toString();

		return ret;
	}
}
