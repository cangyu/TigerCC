package compiler.IR;

public class CSTOREAI extends Normal
{
	public CSTOREAI(Reg r1, Reg r2, Immediate c3)
	{
		super(Operation.cstoreAI, r1, r2, c3);
	}
}
