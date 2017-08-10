package compiler.SymbolTable;

import compiler.Typing.*;

public final class FuncEntry extends Entry
{
	public FuncEntry(Function func_type)
	{
		super(func_type);
	}
}
