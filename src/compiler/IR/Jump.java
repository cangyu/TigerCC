package compiler.IR;

public class Jump extends Quad
{
	public Jump(Label l)
	{
		super(-1, null, null, l);
	}

	@Override
	public String toString()
	{
		return "j ".intern() + result.toString();
	}

}
