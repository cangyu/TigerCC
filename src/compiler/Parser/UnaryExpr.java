package compiler.Parser;

public class UnaryExpr
{
	public static final int postfix = 0;
	public static final int inc = 1;
	public static final int dec = 2;
	public static final int address = 3;
	public static final int dereference = 4;
	public static final int positive = 5;
	public static final int negative = 6;
	public static final int bit_not = 7;
	public static final int not = 8;
	public static final int sizeof = 9;

	public int type;
	public Object elem;

	public UnaryExpr(int t, Object c)
	{
		type = t;
		elem = c;
	}
}
