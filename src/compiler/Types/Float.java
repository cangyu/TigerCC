package compiler.Types;

public class Float extends Type
{
	public Float()
	{
		super(4);
	}

	public static final Float instance = new Float();

	@Override
	public boolean equals(Type rhs)
	{
		return rhs instanceof Float;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return rhs instanceof Float || rhs instanceof Char || rhs instanceof Int || rhs instanceof Double;
	}
}
