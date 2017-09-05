package compiler.IR;

public class DIV extends Normal
{
	public DIV(Reg r1, Reg r2, Reg r3)
	{
		super(Operation.div, r1, r2, r3);
	}
}
