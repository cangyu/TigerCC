package compiler.Parser;

import compiler.Lexer.*;

public class PrimaryExpr
{
	public static final int identifier = 0;
	public static final int integer_constant = 1;
	public static final int character_constant = 2;
	public static final int real_constant = 3;
	public static final int string = 4;
	public static final int paren_expr = 5;

	public int type;
	public Object elem;

	public PrimaryExpr(Object e)
	{
		if (e instanceof Identifier)
		{
			type = identifier;
			Identifier t = (Identifier) e;
			elem = t.name;
		}
		else if (e instanceof Char)
		{
			type = character_constant;
			Char t = (Char) e;
			elem = t.value;
		}
		else if (e instanceof Int)
		{
			type = integer_constant;
			Int t = (Int) e;
			elem = t.value;
		}
		else if (e instanceof Real)
		{
			type = real_constant;
			Real t = (Real) e;
			elem = t.value;
		}
		else if (e instanceof Str)
		{
			type = string;
			Str t = (Str) e;
			elem = t.lexeme;
		}
		else
		{
			type = paren_expr;// primary-expr ::= (expression)
			elem = e;
		}
	}
}
