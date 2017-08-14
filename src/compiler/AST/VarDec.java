package compiler.AST;

import compiler.Typing.*;

public class VarDec extends Dec
{
	public Type type;
	public String name;
	public Init init;
	public boolean hasAssigned;

	// if the variable is an array
	public boolean isConst;
	public boolean isLval;
	public Object val;

	public VarDec(Type t, String vn, Init it)
	{
		type = t;
		name = vn;
		init = it;
		offset = 0;
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
