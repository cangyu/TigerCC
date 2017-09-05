package compiler.IR;

public class ORI extends Normal
{
	public ORI(Reg r1, Immediate c2, Reg r3)
	{
		super(Operation.orI, r1, c2, r3);
	}
}
