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

	public static void beginScope()
	{
		top = new Env(top);
	}

	public static void endScope()
	{
		top = top.prev;
	}

	public static Env top;
}
