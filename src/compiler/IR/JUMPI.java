package compiler.IR;

public class JUMPI extends ControlFlow
{
	public JUMPI(Label l1)
	{
		super(Opcode.jumpI, null, new OperandList(l1));
	}
}
