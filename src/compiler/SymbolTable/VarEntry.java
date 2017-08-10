package compiler.SymbolTable;

import compiler.Typing.*;

public final class VarEntry extends Entry
{
	public int offset;
	public boolean hasInitialized;
	public boolean isLval;
	public boolean isConst;

	public VarEntry(Type t, int off, boolean init, boolean lval, boolean cnst)
	{
		super(t);
		offset = off;
		hasInitialized = init;
		isLval = lval;
		isConst = cnst;
	}
}
