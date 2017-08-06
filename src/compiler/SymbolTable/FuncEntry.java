package compiler.SymbolTable;

import compiler.Types.*;
import compiler.Types.Void;

public final class FuncEntry extends Entry
{
	public FuncEntry(Function func_type)
	{
		super(func_type);
	}

	public static Type get_ret_type(Function func)
	{
		Function f = func;
		for (;;)
		{
			Type arg = f.argumentType;
			Type ret = f.returnType;
			if (arg instanceof Void)
				return ret;
			else
				f = (Function) ret;
		}
	}
}
