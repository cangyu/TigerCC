package compiler.Parser;

import java.util.*;

public class PostfixExpr
{
	public static final int mparen = 0;
	public static final int paren = 1;
	public static final int dot = 2;
	public static final int ptr = 3;
	public static final int inc = 4;
	public static final int dec = 5;

	class Postfix
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
		elem.add(new Postfix(t, c));
	}
}
