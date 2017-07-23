package compiler.Symbols;

import java.util.*;

public class Env
{
	private Hashtable<Symbol, Entry> tbl;
	protected Env prev;

	public Env(Env pv)
	{
		tbl = new Hashtable<Symbol, Entry>();
		prev = pv;
	}

	public void put(Symbol s, Entry e)
	{
		tbl.put(s, e);
	}

	public Entry get(Symbol s)
	{
		Env e = this;
		while (e != null)
		{
			if (tbl.containsKey(s))
				return e.tbl.get(s);
			else
				e = e.prev;
		}
		return null;
	}

	public static void beginScope(Env e)
	{
		e = new Env(e);
	}

	public static void endScope(Env e)
	{
		e = e.prev;
	}
}
