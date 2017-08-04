package compiler.SymbolTable;

import compiler.Types.*;

public final class VarEntry extends Entry
{
	public int offset;
	public boolean hasInitialized;
	public boolean isLval;
	public boolean isConst;

	public VarEntry(Type t, Env e, int off, boolean init)
	{
		super(t, e);
		offset = off;
		hasInitialized = init;
	}
}
