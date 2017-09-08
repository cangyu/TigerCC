package compiler.IR;

public class Mem extends Operand
{
	public int offset;

	public Mem(int o)
	{
		offset = o;
	}

	public String toString()
	{
			return "$" + offset;
	}
}
