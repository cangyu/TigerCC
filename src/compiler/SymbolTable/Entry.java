package compiler.SymbolTable;

import compiler.Types.*;

public class Entry
{
	public Type type;
	public Env scope;

	public Entry(Type t)
	{
		type = t;
	}
}
