package compiler.Symbols;

//not inherit from pointer, as pointer can change where it points to, but array can not
public final class Array extends Type
{
	public int elem_num;
	public Type elem_type;

	public Array(int num, Type t)
	{
		super(num * t.size);
		
		elem_num = num;
		elem_type = t;
	}
}
