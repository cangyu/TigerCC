package compiler.SymbolTable;

import compiler.Types.*;
import java.util.*;

public final class FuncEntry extends Entry
{

	public FuncEntry(Function func_type, Env belong)
	{
		super(func_type, belong);
	}
}
