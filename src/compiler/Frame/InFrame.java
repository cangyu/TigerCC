package compiler.Frame;

import compiler.IR.*;

public class InFrame extends Access
{
	public Frame base;
	public int offset;

	public InFrame(Frame x, int y)
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
