package compiler.IR;

public class XORI extends Normal
{
	public XORI(Reg r1, Immediate c2, Reg r3)
	{
		super(Operation.xorI, r1, c2, r3);
	}
}
