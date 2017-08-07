package compiler.Types;

public class Double extends Type
{
	public Double()
	{
		super(8);
	}

	@Override
	public boolean equals(Type rhs)
	{
		return rhs instanceof Double;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return rhs instanceof Double || rhs instanceof Float || rhs instanceof Char || rhs instanceof Int;
	}
}
