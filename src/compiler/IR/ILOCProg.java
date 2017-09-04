package compiler.IR;

import java.util.LinkedList;

public class ILOCProg
{
	public LinkedList<Ins> tac_list;

	public ILOCProg()
	{
		tac_list = new LinkedList<Ins>();
	}

	public void add_label(Label x)
	{
		tac_list.add(new Ins(x));
	}

	public void add_oper(Operation x)
	{
		tac_list.add(new Ins(x));
	}

	public void add_lbl_oper(Label x, Operation y)
	{
		tac_list.add(new Ins(x, y));
	}
}
