package compiler.IR;

import java.util.*;

public class Instruction
{
	public Label label;
	public LinkedList<Operation> oper_list;

	public Instruction()
	{
		label = null;
		oper_list = new LinkedList<Operation>();
	}

	public Instruction(Label x)
	{
		label = x;
		oper_list = new LinkedList<Operation>();
	}

	public Instruction(Label x, Operation... ops)
	{
		label = x;
		oper_list = new LinkedList<Operation>();
		for (Operation op : ops)
			oper_list.add(op);
	}
}
