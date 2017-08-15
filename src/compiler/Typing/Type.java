package compiler.Typing;

public abstract class Type
{
	public int width;
	public boolean complete, visited;

	public Type(int n)
	{
		width = n;
		visited = false;
	}

	public abstract boolean equals(Type rhs);

	public abstract boolean isConvertableTo(Type rhs);

	public static boolean numeric(Type p)
	{
		return p instanceof Char || p instanceof Int || p instanceof FP;
	}

	public static boolean integer(Type p)
	{
		return p instanceof Char || p instanceof Int;
	}

	public static boolean arith(Type p)
	{
		return Type.numeric(p) || p instanceof Pointer || p instanceof Array;
	}

	public static boolean logic(Type p)
	{
		return Type.integer(p) || p instanceof Pointer;
	}

	public static Type max(Type p1, Type p2)
	{
		if (!numeric(p1) || !numeric(p2))
			return null;

		if (p1 instanceof FP || p2 instanceof FP)
			return FP.instance;
		else if (p1 instanceof Int || p2 instanceof Int)
			return Int.instance;
		else
			return Char.instance;
	}
	
	public abstract String toString();
}
