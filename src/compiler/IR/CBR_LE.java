package compiler.IR;

public class CBR_LE extends ControlFlow
{
	public CBR_LE(Immediate cc1, Label l2, Label l3)
	{
		super(Operation.cbr_LE, cc1, l2, l3);
	}
}
