package compiler.IR;

public class Mem extends Operand
{
	public int offset;
	public Operand base;

	public Mem(Operand t)
	{
		base = t;
		offset = 0;
	}

	public Mem(int o)
	{
		base = null;
		offset = o;
	}

	public Mem(Operand t, int o)
	{
		base = t;
		offset = o;
	}

	public String toString()
	{
		if (offset == 0)
			return base.toString();
		else
			return base.toString() + "[" + offset + "]";
	}
}
