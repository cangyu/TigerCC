package compiler.IR;

public class PHI extends Normal
{
	public PHI(Reg rm, Reg...regs)
	{
		super(Operation.phi, new OperandList(regs), new OperandList(rm));
	}
}
