package compiler.IR;

public class BinOp extends Quad
{
	public BinOp(int o, Operand l, Operand r, Operand d)
	{
		super(o, l, r, d);
	}

	public String toString()
	{
		String ret = "".intern();
		ret += arg1.toString();
		ret += " ".intern();
		ret += Quad.get_op(op);
		ret += " ".intern();
		ret += arg2.toString();
		ret += " -> ".intern();
		ret += result.toString();
		return ret;
	}
}
