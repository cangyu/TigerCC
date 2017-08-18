package compiler.Parser;

import compiler.Lexer.*;

public class PrimaryExpr extends Expr
{
	public static final int identifier = 0;
	public static final int integer_constant = 1;
	public static final int character_constant = 2;
	public static final int real_constant = 3;
	public static final int string = 4;
	public static final int paren_expr = 5;

	public int type;
	public Object elem;

	public PrimaryExpr(Token tk)
	{
		if (tk.tag == Token.ID)
		{
			type = identifier;
			elem = tk.content; // String
		}
		else if (tk.tag == Token.CH)
		{
			type = character_constant;
			elem = tk.content; // Character
		}
		else if (tk.tag == Token.NUM)
		{
			type = integer_constant;
			elem = tk.content; // Integer
		}
		else if (tk.tag == Token.REAL)
		{
			type = real_constant;
			elem = tk.content; // Float
		}
		else if (tk.tag == Token.STR)
		{
			type = string;
			elem = tk.content; // String
		}
		else
		{
			type = -1;
			elem = null;
		}
	}

	public PrimaryExpr(Expr e) // primary-expression ::= '(' expression ')'
	{
		type = paren_expr;
		elem = e;
	}
}
