package compiler.IR;

public class STORE extends Normal
{
	public STORE(Reg r1, Reg r2)
	{
		super(Operation.store, new OperandList(r1), new OperandList(r2));
	}
}
