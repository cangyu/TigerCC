package compiler.AST;

import compiler.Types.*;

public class VarDec extends Dec
{
	public Type type;
	public String name;
	public Init init;

	public VarDec(Type t, String vn)
	{
		type = t;
		name = vn;
	}

	public VarDec(Type t, String vn, Init it)
	{
		type = t;
		name = vn;
		init = it;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		// TODO Auto-generated method stub

	}
}
