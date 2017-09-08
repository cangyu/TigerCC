package compiler.IR;

public class Branch extends Quad
{
	// Here I defined 3 kinds of conditional branch:
	// Branch(-1, x, null, L) -> if x goto L
	// Branch(-1, null, x, L) -> if (not x) goto L
	// Branch(op, x, y, L) -> if (x op y) goto L
	public Branch(Operand l, Operand r, Label lbl)
	{
		super(-1, l, r, lbl);
	}

	public Branch(int o, Operand l, Operand r, Label lbl)
	{
		super(o, l, r, lbl);
	}

	@Override
	public String toString()
	{
		if (op == -1)
		{
			if(arg1!=null)
				return "if " + arg1.toString() + " ? goto " + result.toString();
			else
				return "if not " + arg2.toString() + " ? goto " + result.toString();
		}
		else
			return "if " + arg1.toString() + " " +  Quad.get_op(op) + " " + arg2.toString() + " ? goto " + result.toString();
	}
}
