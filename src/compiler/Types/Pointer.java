package compiler.Types;

public class Pointer extends Type
{
	public Type elem_type;

	public Pointer(Type t)
	{
		super(4);
		elem_type = t;
	}

	@Override
	public boolean equals(Type rhs)
	{
		return rhs instanceof Pointer;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return rhs instanceof Pointer || rhs instanceof Int;
	}
}
