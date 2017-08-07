package compiler.Types;

public final class Int extends Type
{
	public Int()
	{
		super(4);
	}
	
	public static final Int instance = new Int();

	@Override
	public boolean equals(Type rhs)
	{
		return rhs instanceof Int;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return rhs instanceof Int || rhs instanceof Char || rhs instanceof Float || rhs instanceof Pointer;
	}
}
