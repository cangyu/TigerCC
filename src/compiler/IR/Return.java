package compiler.IR;

public class Return extends Quad
{
	public Return(Operand v)
	{
		super(-1, null, null, v);
	}
	
	public Return()
	{
		super(-1, null, null, null);
	}

	@Override
	public String toString()
	{
		if(result!=null)
			return "return ".intern() + result.toString();
		else
			return "return".intern();
	}
}
