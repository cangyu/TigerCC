package compiler.IR;

public class LOADAO extends Normal
{
	public LOADAO(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.loadAO, r1, r2, r3);
	}
}
