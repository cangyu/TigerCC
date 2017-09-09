package compiler.MIPS;

import compiler.Frame.Access;
import compiler.IR.Mem;
import compiler.IR.Operand;

public class InFrame extends Access
{
	public MIPSFrame base;
	public int offset;

	public InFrame(MIPSFrame x, int y)
	{
		base = x;
		offset = y;
	}

	@Override
	public Operand access()
	{
		return new Mem(base.FP(), offset);
	}
}
