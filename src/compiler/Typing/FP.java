package compiler.Typing;

import compiler.Lexer.Token;

public class FP extends Type
{
	public FP()
	{
		super(4);
		complete = true;
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

	@Override
	public String toString()
	{
		return Token.raw_rep(Token.FLOAT);
	}
}
