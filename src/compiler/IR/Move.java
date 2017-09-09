package compiler.IR;

public class Move extends Quad
{
	public Move(Operand src, Operand dst)
	{
		super(-1, src, dst, null);
	}

	@Override
	public String toString()
	{
		return arg1.toString() + " -> " + arg2.toString();
	}
}
