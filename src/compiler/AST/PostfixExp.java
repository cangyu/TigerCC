package compiler.AST;

import java.util.LinkedList;
import compiler.Types.Type;

public class PostfixExp extends Exp
{
	public PrimaryExp pe;
	public LinkedList<PostfixElem> elem;

	public PostfixExp(PrimaryExp x)
	{
		super();
		pe = x;
		elem = new LinkedList<PostfixElem>();
	}

	public void add_elem(int cat, Exp e, String n, Type t)
	{
		elem.add(new PostfixElem(cat, e, n, t));
	}

	public class PostfixElem
	{
		public int category;
		public Exp exp;
		public String id;
		public Type cur_type;

		public PostfixElem(int c, Exp e, String s, Type t)
		{
			category = c;
			exp = e;
			id = s;
			cur_type = t;
		}
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		// TODO Auto-generated method stub

	}
}
