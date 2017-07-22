package compiler.Symbols;

import java.util.LinkedList;

public class ArrayInitializer extends Type
{
	public LinkedList<Type> comp;

	public ArrayInitializer()
	{
		super(0);
		comp = new LinkedList<Type>();
	}

	public void add(Type x)
	{
		comp.add(x);
		size += x.size;
	}
}
