package compiler.IR;

import java.util.*;

public class IRCode
{
	public class Ins
	{
		public Label lbl;
		public Quad oper;

		public Ins(Label x)
		{
			lbl = x;
			oper = null;
		}

		public Ins(Quad x)
		{
			lbl = null;
			oper = x;
		}

		public Ins(Label x, Quad y)
		{
			lbl = x;
			oper = y;
		}

		public String toString()
		{
			String ret = "".intern();
			if (lbl != null)
				ret += lbl.toString();
			if (oper != null)
				ret += oper.toString();
			ret += "\n".intern();
			return ret;
		}
	}

	public LinkedList<Ins> tac_list;

	public IRCode()
	{
		tac_list = new LinkedList<Ins>();
	}

	public void add_label(Label x)
	{
		tac_list.add(new Ins(x));
	}

	public void add_oper(Quad x)
	{
		tac_list.add(new Ins(x));
	}

	public void add_lbl_oper(Label x, Quad y)
	{
		tac_list.add(new Ins(x, y));
	}

	public String toString()
	{
		String ret = "".intern();
		ListIterator<Ins> lit = tac_list.listIterator();
		while (lit.hasNext())
			ret += lit.next().toString();
		return ret;
	}
}
