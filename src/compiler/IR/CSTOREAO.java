package compiler.IR;

public class CSTOREAO extends Normal
{
	public CSTOREAO(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.cstoreAO, r1, r2, r3);
	}
}
