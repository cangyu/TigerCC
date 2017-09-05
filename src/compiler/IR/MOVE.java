package compiler.IR;

public class MOVE extends Operation
{
	public MOVE(Reg r1, Reg r2)
	{
		super(Operation.move, r1, r2);
	}
}
