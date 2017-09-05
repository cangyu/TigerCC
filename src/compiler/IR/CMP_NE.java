package compiler.IR;

public class CMP_NE extends ControlFlow
{
	public CMP_NE(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.cmp_NE, r1, r2, r3);
	}
}
