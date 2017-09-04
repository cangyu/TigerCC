package compiler.IR;

public class CLOADAI extends Normal
{
	public CLOADAI(Reg r1, Immediate c2, Reg r3)
	{
		super(Operation.cloadAI, new OperandList(r1, c2), new OperandList(r3));
	}
}
