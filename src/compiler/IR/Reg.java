package compiler.IR;

public class Reg extends Operand
{
	public int index;

	public Reg()
	{
		index = ++cnt;
	}

	public Reg(int off)
	{
		index = off / 4;
	}

	private static int cnt = 0;
}
