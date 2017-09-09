package compiler.IR;

public abstract class Quad
{
	public int op;
	public Operand arg1, arg2, result;

	public Quad(int x, Operand lhs, Operand rhs, Operand ans)
	{
		op = x;
		arg1 = lhs;
		arg2 = rhs;
		result = ans;
	}

	public abstract String toString();

	public static final int nop = 0;
	public static final int mult = 1;
	public static final int div = 2;
	public static final int mod = 3;
	public static final int add = 6;
	public static final int sub = 7;
	public static final int lshift = 11;
	public static final int rshift = 13;
	public static final int and = 15;
	public static final int or = 17;
	public static final int xor = 19;
	public static final int move = 38;
	public static final int jump = 39;
	public static final int cbr = 41;
	public static final int cmp_LT = 43;
	public static final int cmp_LE = 44;
	public static final int cmp_EQ = 45;
	public static final int cmp_NE = 46;
	public static final int cmp_GT = 47;
	public static final int cmp_GE = 48;

	public static String get_op(int x)
	{
		switch (x)
		{
		case nop:
			return "NOP".intern();
		case mult:
			return "MUL".intern();
		case div:
			return "DIV".intern();
		case add:
			return "ADD".intern();
		case sub:
			return "SUB".intern();
		case lshift:
			return "SHL".intern();
		case rshift:
			return "SHR".intern();
		case and:
			return "AND".intern();
		case or:
			return "OR".intern();
		case xor:
			return "XOR".intern();
		case move:
			return "MOVE".intern();
		case jump:
			return "JMP".intern();
		case cbr:
			return "CBR".intern();
		case cmp_LT:
			return "LT".intern();
		case cmp_LE:
			return "LE".intern();
		case cmp_EQ:
			return "EQ".intern();
		case cmp_NE:
			return "NE".intern();
		case cmp_GT:
			return "GT".intern();
		case cmp_GE:
			return "GE".intern();
		default:
			return "".intern();
		}
	}
}
