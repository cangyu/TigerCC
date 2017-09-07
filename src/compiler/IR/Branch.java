package compiler.IR;

public class Branch extends Quad
{
	public Branch(int o, Label lbl, Operand l, Operand r)
	{
		super(o, l, r, lbl);
	}

	@Override
	public String toString()
	{
		return arg1.toString() + Quad.get_op(op) + arg2.toString() + "? goto ".intern() + result.toString();
	}
}
