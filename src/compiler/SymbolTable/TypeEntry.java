package compiler.SymbolTable;

import compiler.Types.*;

public final class TypeEntry extends Entry
{
	public Type actual;

	public TypeEntry(Type literal_type)
	{
		super(literal_type);
	}

	public void set_actual(Type ac)
	{
		actual = ac;
	}
}
