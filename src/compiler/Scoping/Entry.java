package compiler.Scoping;

import compiler.AST.*;
import compiler.Typing.*;

public class Entry
{
	public static final int entry_var = 0;
	public static final int entry_func = 1;
	public static final int entry_type = 2;

	public int type;
	public ASTNode mirror;
	public Type actual;

	public Entry(int tp, ASTNode m)
	{
		type = tp;
		mirror = m;
		actual = null;
	}

	public Entry(VarDec vd)
	{
		type = entry_var;
		mirror = vd;
		actual = null;
	}

	public Entry(Type tp)
	{
		type = entry_type;
		mirror = null;
		actual = tp;
	}

	public Entry(Function func, FuncDec fd)
	{
		type = entry_func;
		actual = func;
		mirror = fd;
	}

	public void set_actual_type(Type tp) // used for 'typedef'
	{
		actual = tp;
	}
}
