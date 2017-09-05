package compiler.IR;

public class MULT extends Normal
{
	public MULT(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.mult, r1, r2, r3);
	}
}
