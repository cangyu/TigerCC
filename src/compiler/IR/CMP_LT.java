package compiler.IR;

public class CMP_LT extends ControlFlow
{
	public CMP_LT(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.cmp_LT, new OperandList(r1, r2), new OperandList(r3));
	}
}
