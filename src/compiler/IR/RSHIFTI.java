package compiler.IR;

public class RSHIFTI extends Normal
{
	public RSHIFTI(Reg r1, Immediate c2, Reg r3)
	{
		super(Operation.rshiftI, r1, c2, r3);
	}
}
