package compiler.Typing;

import compiler.Lexer.Token;

public final class Char extends Type
{
	public Char()
	{
		super(1);
		complete = true;
	}

	public static Char instance = new Char();

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
