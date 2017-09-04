package compiler.IR;

public class CMP_EQ extends ControlFlow
{
	public CMP_EQ(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.cmp_EQ, new OperandList(r1, r2), new OperandList(r3));
	}
}
