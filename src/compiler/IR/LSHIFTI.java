package compiler.IR;

public class LSHIFTI extends Normal
{
	public LSHIFTI(Reg r1, Immediate c2, Reg r3)
	{
		super(Operation.lshiftI, r1, c2, r3);
	}
}
