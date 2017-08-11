package compiler.AST;

import compiler.Typing.*;

public class VarDec extends Dec
{
	public Type type;
	public String name;
	public Init init;
	public int start_pos;

	public VarDec(Type t, String vn, Init it)
	{
		type = t;
		name = vn;
		init = it;
		start_pos = 0;
	}

	public void set_pos(int p)
	{
		start_pos = p;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
