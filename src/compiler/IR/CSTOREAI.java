package compiler.IR;

public class CSTOREAI extends Normal
{
	public CSTOREAI(Reg r1, Reg r2, Immediate c3)
	{
		super(Operation.cstoreAI, new OperandList(r1), new OperandList(r2, c3));
	}
}
