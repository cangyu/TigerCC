package compiler.IR;

public class C2I extends Normal
{
	public C2I(Reg r1, Reg r2)
	{
		super(Operation.c2i, r1, r2);
	}
}
