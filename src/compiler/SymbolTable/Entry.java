package compiler.SymbolTable;

import compiler.Types.*;

public class Entry
{
	public Type type;
	public Env domain;

	public Entry(Type t, Env e)
	{
		type = t;
		domain = e;
	}
}
