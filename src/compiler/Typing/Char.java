package compiler.Typing;

import compiler.Lexer.Token;

public final class Char extends Type
{
	private Char()
	{
		super(1);
		complete = true;
	}

	private static Char instance;

	public static Char getInstance()
	{
		if (instance == null)
			instance = new Char();

		return instance;
	}

	@Override
	public boolean equals(Type rhs)
	{
		return rhs instanceof Char;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return rhs instanceof Char || rhs instanceof Int || rhs instanceof FP;
	}

	@Override
	public String toString()
	{
		return Token.raw_rep(Token.CHAR);
	}
}
