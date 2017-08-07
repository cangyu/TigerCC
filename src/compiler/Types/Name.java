package compiler.Types;

public class Name extends Type
{
	public String name;

	public Name(String n)
	{
		super(0);
		name = n;
	}

	@Override
	public boolean equals(Type rhs)
	{
		return false;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return false;
	}
}
