package compiler.IR;

public class XOR extends Normal
{
	public XOR(Reg r1, Reg r2, Reg r3)
	{
		super(Opcode.xor, new OperandList(r1, r2), new OperandList(r3));
	}
}
