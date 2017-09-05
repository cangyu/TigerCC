package compiler.IR;

public class CBR_GE extends ControlFlow
{
	public CBR_GE(Immediate cc1, Label l2, Label l3)
	{
		super(Operation.cbr_GE, cc1, l2, l3);
	}
}
