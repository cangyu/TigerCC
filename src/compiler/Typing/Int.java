package compiler.Typing;

import compiler.Lexer.Token;

public final class Int extends Type
{
	public Int()
	{
		super(4);
		complete = true;
	}

	public static final Int instance = new Int();

	@Override
	public boolean equals(Type rhs)
	{
		return rhs instanceof Int;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return rhs instanceof Int || rhs instanceof Char || rhs instanceof FP || rhs instanceof Pointer;
	}
	
    @Override
    public String toString()
    {
        return Token.raw_rep(Token.INT);
    }
}
