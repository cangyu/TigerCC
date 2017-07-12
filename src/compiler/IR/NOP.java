package compiler.IR;

public class NOP extends NormalOp
{
	public NOP()
	{
		super(Opcode.nop, null, null);
	}
}
