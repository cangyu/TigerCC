package compiler.IR;

public class JUMP extends ControlFlow
{
	public JUMP(Reg r1)
	{
		super(Operation.jump, null, new OperandList(r1));
	}
}
