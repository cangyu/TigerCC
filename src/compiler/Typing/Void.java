package compiler.Typing;

import compiler.Lexer.*;

public final class Void extends Type
{
	public Void()
	{
		super(1);
		complete = true;
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

    @Override
    public String toString()
    {
        return Token.raw_rep(Token.VOID);
    }
}
