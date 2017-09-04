package compiler.IR;

public class DIV extends Normal
{
	public DIV(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.div, new OperandList(r1, r2), new OperandList(r3));
	}
}
