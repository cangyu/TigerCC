package compiler.IR;

public class RDIVI extends Normal
{
	public RDIVI(Reg r1, Immediate c2, Reg r3)
	{
		super(Operation.rdivI, r1, c2, r3);
	}
}
