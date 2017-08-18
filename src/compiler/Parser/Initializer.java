package compiler.Parser;

import java.util.LinkedList;

public class Initializer
{
	public static final int assign = 0;
	public static final int list = 1;

	public int type;
	public Expr ae;
	public LinkedList<Initializer> comp;

	public Initializer(Expr x)
	{
		type = assign;
		ae = x;
	}

	public Initializer()
	{
		type = list;
		comp = new LinkedList<Initializer>();
	}

	public void add_initializer(Initializer x)
	{
		comp.add(x);
	}
}
