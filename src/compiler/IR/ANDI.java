package compiler.IR;

public class ANDI extends Normal
{
	public ANDI(Reg r1, Immediate c2, Reg r3)
	{
		super(Operation.andI, new OperandList(r1, c2), new OperandList(r3));
	}
}
