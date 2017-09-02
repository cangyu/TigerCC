package compiler.IR;

public class STOREAO extends Normal
{
	public STOREAO(Reg r1, Reg r2, Reg r3)
	{
		super(Opcode.storeAO, new OperandList(r1), new OperandList(r2, r3));
	}
}
