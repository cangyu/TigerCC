package compiler.IR;

public class CSTORE extends Normal
{
	public CSTORE(Reg r1, Reg r2)
	{
		super(Opcode.cstore, new OperandList(r1), new OperandList(r2));
	}
}
