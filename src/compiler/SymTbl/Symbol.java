package compiler.SymTbl;

import java.util.*;

//Fly-weight pattern: String Interning
public class Symbol
{
	public String name;

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

	private static Hashtable<String, Symbol> dict = new Hashtable<String, Symbol>();
}
