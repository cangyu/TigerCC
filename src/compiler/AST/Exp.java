package compiler.AST;

import compiler.Typing.*;

public abstract class Exp extends ASTNode
{
	public Type type;
	public boolean isConst;
	public Object value; // valid only when 'isConst' == true
	public boolean hasInitialized;
	public boolean isLvalue;

	public Exp()
	{
		type = null;
		isConst = false;
		value = null;
		hasInitialized = false;
		isLvalue = false;
	}

	public void decorate(Type t, boolean cons, boolean init, boolean lval)
	{
		type = t;
		isConst = cons;
		hasInitialized = init;
		isLvalue = lval;
	}

	public void set_value(Object val)
	{
		value = val; // Valid when 'isConst' == true
	}
}
