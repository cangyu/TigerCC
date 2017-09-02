package compiler.IR;

public class TBL extends ControlFlow
{
	public TBL(Reg r1, Label l1)
	{
		super(Opcode.tbl, new OperandList(r1, l1), null);
	}
}
