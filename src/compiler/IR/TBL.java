package compiler.IR;

public class TBL extends ControlFlow
{
	public TBL(Reg r1, Label l1)
	{
		super(Operation.tbl, r1, l1);
	}
}
