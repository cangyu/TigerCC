package compiler.IR;

public class CSTORE extends Normal
{
	public CSTORE(Reg r1, Reg r2)
	{
		super(Operation.cstore, r1, r2);
	}
}
