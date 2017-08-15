package compiler.Typing;

//not inherit from pointer, as pointer can change where it points to, but array can not
public final class Array extends Type
{
	public int elem_num;
	public Type elem_type;

	public Array(int num, Type t)
	{
		super(num * t.width);
		elem_num = num;
		elem_type = t;
		complete = true;
	}

	@Override
	public boolean equals(Type rhs)
	{
		return rhs instanceof Array;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return false;
	}
}
