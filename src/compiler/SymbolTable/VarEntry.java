package compiler.SymbolTable;

import compiler.Types.*;

public final class VarEntry extends Entry
{
	public int offset;
	public boolean hasInitialized;
	public boolean isLval;
	public boolean isConst;

	public VarEntry(Type t, int off, boolean init)
	{
		super(t);
		offset = off;
		hasInitialized = init;
	}
}
