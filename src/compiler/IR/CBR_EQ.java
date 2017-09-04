package compiler.IR;

public class CBR_EQ extends ControlFlow
{
	public CBR_EQ(Immediate cc1, Label l2, Label l3)
	{
		super(Operation.cbr_EQ, new OperandList(cc1), new OperandList(l2, l3));
	}
}
