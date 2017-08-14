package compiler.Scoping;

import compiler.AST.ASTNode;
import compiler.Typing.*;

public class Entry
{
	public static final int ety_var = 0;
	public static final int ety_func = 1;
	public static final int ety_type = 2;

	public int type;
	public ASTNode mirror;
	public Type actual;

	public Entry(int tp, ASTNode m)
	{
		type = tp;
		mirror = m;
		actual = null;
	}

	public void set_actual_type(Type tp) // used for 'typedef'
	{
		actual = tp;
	}
}
