package compiler.Typing;

import compiler.Lexer.*;

public final class Void extends Type
{
	private Void()
	{
		super(1);
		complete = true;
	}

	private static Void instance;

	public static Void getInstance()
	{
		if (instance == null)
			instance = new Void();

		return instance;
	}

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

	@Override
	public String toString()
	{
		return Token.raw_rep(Token.VOID);
	}
}
