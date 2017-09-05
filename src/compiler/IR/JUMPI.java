package compiler.IR;

public class JUMPI extends ControlFlow
{
	public JUMPI(Label l1)
	{
		super(Operation.jumpI, l1);
	}
}
