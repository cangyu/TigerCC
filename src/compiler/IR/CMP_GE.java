package compiler.IR;

public class CMP_GE extends ControlFlow
{
	public CMP_GE(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.cmp_GE, r1, r2, r3);
	}
}
