package compiler.IR;

public class STORE extends Normal
{
	public STORE(Reg r1, Reg r2)
	{
		super(Operation.store, r1, r2);
	}
}
