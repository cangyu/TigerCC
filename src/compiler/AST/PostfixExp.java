package compiler.AST;

import java.util.LinkedList;

import compiler.Typing.Type;

public class PostfixExp extends Exp
{
	public class PostfixElem
	{
		public static final int post_idx = 0;
		public static final int post_call = 1;
		public static final int post_dot = 2;
		public static final int post_arrow = 3;
		public static final int post_inc = 4;
		public static final int post_dec = 5;

		public int category;
		public CommaExp exp;
		public String id;
		public Type cur_type; // current type of the whole postfix-exp

		public PostfixElem(int c, CommaExp e, String s, Type t)
		{
			category = c;
			exp = e;
			id = s;
			cur_type = t;
		}
	}

	public PrimaryExp pe;
	public LinkedList<PostfixElem> elem;

	public PostfixExp(PrimaryExp x)
	{
		super();
		pe = x;
		elem = new LinkedList<PostfixElem>();
	}

	public void add_elem(int cat, CommaExp e, String n, Type t)
	{
		elem.add(new PostfixElem(cat, e, n, t));
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{

	}
}
