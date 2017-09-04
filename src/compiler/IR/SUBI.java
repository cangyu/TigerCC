package compiler.IR;

public class SUBI extends Normal
{
	public SUBI(Reg r1, Immediate c2, Reg r3)
	{
		super(Operation.subI, new OperandList(r1, c2), new OperandList(r3));
	}
}
