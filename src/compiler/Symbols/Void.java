package compiler.Symbols;

public final class Void extends Type
{
	public Void()
	{
		super(1);
	}

	public static Void instance = new Void();
}
