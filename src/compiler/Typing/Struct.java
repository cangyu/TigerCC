package compiler.Typing;

import java.util.*;
import compiler.Lexer.Token;
import compiler.Scoping.Symbol;

public final class Struct extends Record
{
	public Hashtable<Symbol, Integer> field_offset;

	public Struct()
	{
		super();
		field_offset = new Hashtable<Symbol, Integer>();
	}

	public void add_record(String n, Type t)
	{
		add_member(n, t);
		field_offset.put(Symbol.getSymbol(n), new Integer(width));
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

	@Override
	public int get_member_offset(String m)
	{
		Symbol csym = Symbol.getSymbol(m);
		if (!field.containsKey(csym))
			return -1;
		else
			return field_offset.get(csym).intValue();
	}
}
