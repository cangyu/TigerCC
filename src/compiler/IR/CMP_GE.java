package compiler.IR;

public class CMP_GE extends ControlFlow
{
	public CMP_GE(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.cmp_GE, new OperandList(r1, r2), new OperandList(r3));
	}
}
