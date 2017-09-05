package compiler.IR;

public class CBR_GT extends ControlFlow
{
	public CBR_GT(Immediate cc1, Label l2, Label l3)
	{
		super(Operation.cbr_GT, cc1, l2, l3);
	}
}
