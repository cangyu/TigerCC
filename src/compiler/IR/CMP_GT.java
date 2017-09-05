package compiler.IR;

public class CMP_GT extends ControlFlow
{
	public CMP_GT(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.cmp_GT, r1, r2, r3);
	}
}
