package compiler.IR;

public class Reg extends Operand
{
	public int index;

	public Reg()
	{
		index = ++cnt;
	}

	private static int cnt = 0;
}
