package compiler.IR;

public class STOREAO extends Normal
{
	public STOREAO(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.storeAO, r1, r2, r3);
	}
}
