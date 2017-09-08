package compiler.IR;

public class Param extends Quad
{
	public Param(Operand arg)
	{
		super(-1, null, null, arg);
	}

	public String toString()
	{
		return "param ".intern() + result.toString();
	}

}
