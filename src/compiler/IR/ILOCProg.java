package compiler.IR;

import java.util.LinkedList;

public class ILOCProg
{
	public LinkedList<Instruction> tac_list;

	public ILOCProg()
	{
		tac_list = new LinkedList<Instruction>();
	}

	public void add_instruction(Instruction x)
	{
		tac_list.add(x);
	}
}
