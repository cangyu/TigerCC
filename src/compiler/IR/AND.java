package compiler.IR;

public class AND extends Normal
{
	public AND(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.and, new OperandList(r1, r2), new OperandList(r3));
	}
}
