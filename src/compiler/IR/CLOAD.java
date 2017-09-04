package compiler.IR;

public class CLOAD extends Normal
{
	public CLOAD(Reg r1, Reg r2)
	{
		super(Operation.cload, new OperandList(r1), new OperandList(r2));
	}
}
