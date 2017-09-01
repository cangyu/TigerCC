package compiler.Typing;

import compiler.Lexer.Token;

public final class Struct extends Record
{
	public Struct()
	{
		super();
	}

	public void add_record(String n, Type t)
	{
		add_member(n, t);
		width += t.width;
	}

	@Override
	public boolean equals(Type rhs)
	{
		return rhs == this;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return equals(rhs);
	}

	@Override
	public String toString()
	{
		String ret = Token.raw_rep(Token.STRUCT) + " ";
		if (tag != null)
			ret += tag + " ";
		return ret;
	}
}
