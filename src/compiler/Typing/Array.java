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
		if (rhs instanceof Array)
			return elem_type.isConvertableTo(((Array) rhs).elem_type);
		else if (rhs instanceof Pointer)
			return elem_type.isConvertableTo(((Pointer) rhs).elem_type);
		else
			return false;
	}

	@Override
	public String toString()
	{
		return String.format("array of %d %s ", elem_num, elem_type.toString());
	}
}
