package compiler.IR;

public class CBR_EQ extends ControlFlow
{
	public CBR_EQ(Immediate cc1, Label l2, Label l3)
	{
		super(Operation.cbr_EQ, cc1, l2, l3);
	}
}
