package compiler.IR;

public class RSHIFTI extends Normal
{
	public RSHIFTI(Reg r1, Immediate c2, Reg r3)
	{
		super(Operation.rshiftI, new OperandList(r1, c2), new OperandList(r3));
	}
}
