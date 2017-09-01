package compiler.AST;

import compiler.Typing.*;

public class VarDec extends Dec
{
	public Type type;
	public Init init;
	public boolean hasAssigned; // for marking later assignment

	public VarDec(Type t, String vn, Init it, int off)
	{
		super(off, vn);
		type = t;
		init = it;
		hasAssigned = false;
	}

	public void set_pos(int p)
	{
		offset = p;
	}

	public boolean isInitialized()
	{
		return init != null || hasAssigned;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
