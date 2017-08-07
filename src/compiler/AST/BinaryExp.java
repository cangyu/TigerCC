package compiler.AST;

public abstract class BinaryExp extends Exp
{
	public static final int BIT_AND = 0;
	public static final int BIT_XOR = 1;
	public static final int BIT_OR = 2;
	public static final int AND = 3;
	public static final int OR = 4;
	public static final int EQ = 5;
	public static final int NE = 6;
	public static final int LT = 7;
	public static final int GT = 8;
	public static final int LE = 9;
	public static final int GE = 10;
	public static final int SHL = 11;
	public static final int SHR = 12;
	public static final int PLUS = 13;
	public static final int MINUS = 14;
	public static final int TIMES = 15;
	public static final int DIVIDE = 16;
	public static final int MODULE = 17;

	public int op;
	public Exp left, right;

	public BinaryExp(int _t, Exp le, Exp re)
	{
		super();
		op = _t;
		left = le;
		right = re;
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
