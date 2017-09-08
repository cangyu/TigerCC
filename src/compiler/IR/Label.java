package compiler.IR;

public class Label extends Operand
{
	public String name;

	public Label(String x)
	{
		name = x;
		++cnt;
	}

	public Label()
	{
		name = "l".intern() + cnt++;
	}

	public String toString()
	{
		return name;
	}

	private static int cnt = 0;
}
