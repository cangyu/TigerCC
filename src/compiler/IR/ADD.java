package compiler.IR;

public class ADD extends Normal
{
	public ADD(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.add, r1, r2, r3);
	}
}
