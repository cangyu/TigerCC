package compiler.SymbolTable;

import java.util.*;

//Fly-weight pattern: String Interning
public class Symbol
{
	private String name;

	private Symbol(String n)
	{
		name = n;
	}

	public static Symbol getSymbol(String text)
	{
		String unique = text.intern();
		Symbol s = dict.get(unique);

		if (s == null)
		{
			s = new Symbol(unique);
			dict.put(unique, s);
		}

		return s;
	}

	public static void putSymbol(String text)
	{
		String unique = text.intern();
		if (!dict.containsKey(unique))
			dict.put(unique, new Symbol(unique));
	}

	private static Hashtable<String, Symbol> dict = new Hashtable<String, Symbol>();
}
