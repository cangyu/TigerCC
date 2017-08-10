package compiler.Typing;

public final class Void extends Type
{
	public Void()
	{
		super(1);
	}

	public static Void instance = new Void();

	@Override
	public boolean equals(Type rhs)
	{
		return rhs instanceof Void;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return rhs instanceof Void;
	}
}
