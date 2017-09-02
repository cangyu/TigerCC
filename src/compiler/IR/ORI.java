package compiler.IR;

public class ORI extends Normal
{
	public ORI(Reg r1, Immediate c2, Reg r3)
	{
		super(Opcode.orI, new OperandList(r1, c2), new OperandList(r3));
	}
}
