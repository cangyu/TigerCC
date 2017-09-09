package compiler.IR;

public class BinOp extends Quad
{
	public BinOp(int o, Operand l, Operand r, Operand d)
	{
		super(o, l, r, d);
	}

	public String toString()
	{
		String ret = "";
		ret += arg1.toString();
		ret += " ";
		ret += Quad.get_op(op);
		ret += " ";
		ret += arg2.toString();
		ret += " -> ";
		ret += result.toString();
		return ret;
	}
}
