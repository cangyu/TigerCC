package compiler.IR;

public class Temp extends Operand
{
	public int index;

	public Temp()
	{
		index = ++cnt;
	}

	public String toString()
	{
		return "r".intern() + index;
	}

	private static int cnt = 0;
}
