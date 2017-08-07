package compiler.Types;

public abstract class Type
{
	public int width;

	public Type(int n)
	{
		width = n;
	}

	public static boolean numeric(Type p)
	{
		return p instanceof Char || p instanceof Int || p instanceof Float;
	}

	public static Type max(Type p1, Type p2)
	{
		if (!numeric(p1) || !numeric(p2))
			return null;

		if (p1 instanceof Float || p2 instanceof Float)
			return Float.instance;
		else if (p1 instanceof Int || p2 instanceof Int)
			return Int.instance;
		else
			return Char.instance;
	}
	
	public abstract boolean equals(Type rhs);
	
	public abstract boolean isConvertableTo(Type rhs);
}
