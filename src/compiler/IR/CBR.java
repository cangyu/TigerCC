package compiler.IR;

public class CBR extends ControlFlow
{
	public CBR(Reg r1, Label l2, Label l3)
	{
		super(Operation.cbr, r1, l2, l3);
	}
}
