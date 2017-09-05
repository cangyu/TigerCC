package compiler.IR;

public class LSHIFT extends Normal
{
	public LSHIFT(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.lshift, r1, r2, r3);
	}
}
