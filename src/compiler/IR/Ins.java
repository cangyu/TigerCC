package compiler.IR;

public class Ins
{
	public Label lbl;
	public Operation oper;
	
	public Ins(Label x)
	{
		lbl = x;
		oper = null;
	}
	
	public Ins(Operation x)
	{
		lbl = null;
		oper = x;
	}
	
	public Ins(Label x, Operation y)
	{
		lbl = x;
		oper = y;
	}
}
