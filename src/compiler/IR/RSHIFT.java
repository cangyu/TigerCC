package compiler.IR;

public class RSHIFT extends Normal
{
	public RSHIFT(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.rshift, r1, r2, r3);
	}
}
