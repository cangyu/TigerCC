package compiler.IR;

public class STOREAI extends Normal
{
	public STOREAI(Reg r1, Reg r2, Immediate c3)
	{
		super(Operation.storeAI, r1, r2, c3);
	}
}
