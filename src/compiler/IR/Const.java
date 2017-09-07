package compiler.IR;

public class Const extends Operand
{
	public Object val;

	public Const(char x)
	{
		val = new Character(x);
	}

	public Const(int x)
	{
		val = new Integer(x);
	}

	public Const(float x)
	{
		val = new Float(x);
	}
}
