package compiler.IR;

public class CMP_LE extends ControlFlow
{
	public CMP_LE(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.cmp_LE, r1, r2, r3);
	}
}
