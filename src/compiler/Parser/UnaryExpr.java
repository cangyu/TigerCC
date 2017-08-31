package compiler.Parser;

import compiler.Lexer.Token;

public class UnaryExpr extends Expr
{
	public int type;
	public Expr expr;
	public TypeName tpn;

	public UnaryExpr(int t, Expr c)
	{
		type = t;
		expr = c;
		tpn = null;
	}

	public UnaryExpr(TypeName t)
	{
		type = sizeof;
		expr = null;
		tpn = t;
	}

	public static final int inc = 1;
	public static final int dec = 2;
	public static final int address = 3;
	public static final int dereference = 4;
	public static final int positive = 5;
	public static final int negative = 6;
	public static final int bit_not = 7;
	public static final int not = 8;
	public static final int sizeof = 9;

	public static boolean has_unary_heading(Token tk)
	{
		switch (tk.tag)
		{
		case Token.BIT_AND:
		case Token.TIMES:
		case Token.PLUS:
		case Token.MINUS:
		case Token.BIT_NOT:
		case Token.NOT:
		case Token.INC:
		case Token.DEC:
		case Token.SIZEOF:
			return true;
		default:
			return false;
		}
	}
}
