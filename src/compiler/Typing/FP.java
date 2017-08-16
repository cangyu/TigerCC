package compiler.Typing;

import compiler.Lexer.Token;

public class FP extends Type
{
	private FP()
	{
		super(4);
		complete = true;
	}

	private static FP instance;

	public static FP getInstance()
	{
		if (instance == null)
			instance = new FP();

		return instance;
	}

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

	@Override
	public String toString()
	{
		return Token.raw_rep(Token.FLOAT);
	}
}
