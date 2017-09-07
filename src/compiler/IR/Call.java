package compiler.IR;

public class Call extends Quad
{
	public int param_num;

	public Call(Label name, int n)
	{
		super(-1, null, null, name);
		param_num = n;
	}
}
