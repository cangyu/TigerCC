package compiler.Types;

public class FP extends Type
{
	public FP()
	{
		super(8);
	}

	public static FP instance = new FP();

	@Override
	public boolean equals(Type rhs)
	{
		return rhs instanceof FP;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return rhs instanceof FP || rhs instanceof Char || rhs instanceof Int;
	}
}
