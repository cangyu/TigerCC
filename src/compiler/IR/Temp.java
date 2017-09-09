package compiler.IR;

public class Temp extends Operand
{
	public int index;
	public String name;

	public Temp()
	{
		index = ++cnt;
		name = null;
	}

	public Temp(String x)
	{
		index = ++cnt;
		name = x;
	}

	public void set_name(String x)
	{
		name = x;
	}

	public String toString()
	{
		if (name == null)
			return "$t" + index;
		else
			return "$" + name;
	}

	private static int cnt = 0;
}
