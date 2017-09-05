package compiler.IR;

public class CLOAD extends Normal
{
	public CLOAD(Reg r1, Reg r2)
	{
		super(Operation.cload, r1, r2);
	}
}
