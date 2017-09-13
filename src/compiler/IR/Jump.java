package compiler.IR;

public class Jump extends Quad
{
	public boolean is_label;

	public Jump(Label l)
	{
		super(-1, null, null, l);
		is_label = true;
	}

	public Jump(Temp t)
	{
		super(-1, null, null, t);
		is_label = false;
	}

	@Override
	public String toString()
	{
		return "goto " + result.toString();
	}

}
