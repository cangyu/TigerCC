package compiler.IR;

public class CLOADAO extends Normal
{
	public CLOADAO(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.cloadAO, new OperandList(r1, r2), new OperandList(r3));
	}
}
