package compiler.IR;

public class MULTI extends Normal
{
	public MULTI(Reg r1, Immediate c2, Reg r3)
	{
		super(Operation.multI, r1, c2, r3);
	}
}
