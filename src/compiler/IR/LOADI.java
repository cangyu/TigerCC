package compiler.IR;

public class LOADI extends Normal
{
	public LOADI(Immediate c1, Reg r2)
	{
		super(Opcode.loadI, new OperandList(c1), new OperandList(r2));
	}
}
