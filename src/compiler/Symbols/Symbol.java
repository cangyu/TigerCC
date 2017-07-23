package compiler.Symbols;

import java.util.*;

public class Symbol
{
	private String name;

	public Symbol(String n)
	{
		name = n;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public static Symbol getSymbol(String n)
	{
		String unique = n.intern();
		Symbol s = dict.get(unique);
		
		if (s == null)
		{
			s = new Symbol(unique);
			dict.put(unique, s);
		}
		
		return s;
	}

	private static Dictionary<String, Symbol> dict = new Hashtable<String, Symbol>();
}
