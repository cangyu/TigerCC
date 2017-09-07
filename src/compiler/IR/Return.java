package compiler.IR;

public class Return extends Quad
{
	public Return(Operand v)
	{
		super(-1, null, null, v);
	}

	@Override
	public String toString()
	{
		return "return " + result.toString();
	}
}
