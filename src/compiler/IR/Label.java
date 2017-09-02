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

	private static int cnt = 0;
}
