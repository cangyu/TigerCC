package compiler.IR;

public class LOADAI extends Normal
{
	public LOADAI(Reg r1, Immediate c2, Reg r3)
	{
		super(Operation.loadAI, new OperandList(r1, c2), new OperandList(r3));
	}
}
