package compiler.Symbols;

import java.util.*;

public class Env
{
	private Hashtable tbl;
	protected Env prev;
	
	public Env(Env pv)
	{
		tbl = new Hashtable<>();
		prev = pv;
	}
}
