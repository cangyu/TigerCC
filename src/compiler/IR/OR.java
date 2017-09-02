package compiler.IR;

public class OR extends Normal
{
	public OR(Reg r1, Reg r2, Reg r3)
	{
		super(Opcode.or, new OperandList(r1, r2), new OperandList(r3));
	}
}
