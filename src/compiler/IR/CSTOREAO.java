package compiler.IR;

public class CSTOREAO extends Normal
{
	public CSTOREAO(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.cstoreAO, new OperandList(r1), new OperandList(r2, r3));
	}
}
