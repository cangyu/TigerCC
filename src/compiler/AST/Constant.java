package compiler.AST;

import compiler.Lexer.*;

public class Constant extends ASTNode
{
	public static final int const_char = 0;
	public static final int const_int = 1;
	public static final int const_real = 2;

	public int type;
	public Object val;

	public Constant(Object x) throws Exception
	{
		if (x instanceof Char)
		{
			type = const_char;
			Char t = (Char) x;
			val = t.value;
		}
		else if (x instanceof Int)
		{
			type = const_int;
			Int t = (Int) x;
			val = t.value;
		}
		else if (x instanceof Real)
		{
			type = const_real;
			Real t = (Real) x;
			val = t.value;
		}
		else
			throw new Exception("Invalid input object.");
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}

}
