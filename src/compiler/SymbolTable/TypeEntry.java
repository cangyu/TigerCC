package compiler.SymbolTable;

import compiler.Types.*;

public final class TypeEntry extends Entry
{
	public Type actual;
	
	public TypeEntry(Type t)
	{
		super(t);
	}
}
