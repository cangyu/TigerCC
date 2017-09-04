package compiler.IR;

public class MOVE extends Operation
{
	public MOVE(Reg src, Reg dst)
	{
		super(Operation.move, new OperandList(src), new OperandList(dst));
	}
}
