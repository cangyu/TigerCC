package compiler.IR;

public class LOAD extends Normal
{
	public LOAD(Reg r1, Reg r2)
	{
		super(Operation.load, r1, r2);
	}
}
