package compiler.SymbolTable;

import compiler.Typing.*;

public final class TypeEntry extends Entry
{
	public Type actual;

	public TypeEntry(Type literal_type)
	{
		super(literal_type);
		actual = null;
	}

	public void set_actual(Type ac)
	{
		actual = ac;
	}
}
