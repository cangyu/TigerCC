package compiler.IR;

public class Mem extends Operand
{
	public Operand base;
	public int offset;

	public Mem(Operand x, int off)
	{
		base = x;
		offset = off;
	}

	public Mem(Operand x)
	{
		this(x, 0);
	}

	public Mem(int off)
	{
		this(null, off);
	}

	public String toString()
	{
		if (base != null)
			return "Mem[" + base.toString() + ", " + offset + "]";
		else
			return "Mem[" + offset + "]";
	}
}
