package compiler.IR;

public class SUB extends Normal
{
	public SUB(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.sub, r1, r2, r3);
	}
}
