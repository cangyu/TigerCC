package compiler.IR;

public abstract class ControlFlow extends Operation
{
	public ControlFlow(int x, Operand... y)
	{
		super(x, y);
	}
}
