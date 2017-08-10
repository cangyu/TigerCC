package compiler.Typing;

import compiler.AST.BinaryExp;

public abstract class Type
{
	public int width;

	public Type(int n)
	{
		width = n;
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
}
