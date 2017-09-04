package compiler.IR;

public class CBR_NE extends ControlFlow
{
	public CBR_NE(Immediate cc1, Label l2, Label l3)
	{
		super(Operation.cbr_NE, new OperandList(cc1), new OperandList(l2, l3));
	}
}
