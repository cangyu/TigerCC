package compiler.SymbolTable;

import compiler.Types.*;

public final class TypeEntry extends Entry
{
	public Type actual;

	public TypeEntry(Type literal_type, Env belong)
	{
		super(literal_type, belong);
	}

	public void set_actual(Type ac)
	{
		actual = ac;
	}
}
