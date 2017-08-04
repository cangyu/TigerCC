package compiler.Types;

import java.util.LinkedList;

//not inherit from pointer, as pointer can change where it points to, but array can not
public final class Array extends Type
{
	public int elem_num;
	public Type elem_type;
	public LinkedList<Type> comp; //for initializers

	public Array(int num, Type t)
	{
		super(num * t.width);
		elem_num = num;
		elem_type = t;
		comp = new LinkedList<Type>();
	}
	
	public void add_init(Type x)
	{
		comp.add(x);
	}
}
