package compiler.Parser;

import java.util.*;
import compiler.Lexer.Token;

public class PostfixExpr extends Expr
{
	public static final int mparen = 0;
	public static final int paren = 1;
	public static final int dot = 2;
	public static final int ptr = 3;
	public static final int inc = 4;
	public static final int dec = 5;

	public static boolean has_postfix_heading(Token tk)
	{
		switch (tk.tag)
		{
		case Token.LMPAREN:
		case Token.LPAREN:
		case Token.DOT:
		case Token.PTR:
		case Token.INC:
		case Token.DEC:
			return true;
		default:
			return false;
		}
	}

	public class Postfix
	{
		public int type;
		public Object content;

		public Postfix(int t, Object c)
		{
			type = t;
			content = c;
		}
	}

	public PrimaryExpr expr;
	public LinkedList<Postfix> elem;

	public PostfixExpr(PrimaryExpr pe)
	{
		expr = pe;
		elem = new LinkedList<Postfix>();
	}

	public void add_elem(int t, Object c)
	{
		// Type of 'c'
		// mparen, paren : Expr
		// dot, ptr: String
		// inc, dec: null
		elem.add(new Postfix(t, c));
	}
}
