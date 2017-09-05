package compiler.IR;

public class CBR_LT extends ControlFlow
{
	public CBR_LT(Immediate cc1, Label l2, Label l3)
	{
		super(Operation.cbr_LT, cc1, l2, l3);
	}
}
