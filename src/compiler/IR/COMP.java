package compiler.IR;

public class COMP extends ControlFlow
{
	public COMP(Reg r1, Reg r2, Immediate cc3)
	{
		super(Operation.comp, r1, r2, cc3);
	}
}
