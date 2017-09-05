package compiler.IR;

public class AND extends Normal
{
	public AND(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.and, r1, r2, r3);
	}
}
