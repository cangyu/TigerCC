package compiler.IR;

import java.util.LinkedList;

public class IRProg
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
	}

	public LinkedList<Ins> tac_list;

	public IRProg()
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
}
