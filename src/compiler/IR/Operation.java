package compiler.IR;

public abstract class Operation
{
	public int op;
	public Operand oprnd[];

	public Operation(int x, Operand... oprs)
	{
		op = x;
		oprnd = new Operand[oprs.length];
		for (int i = 0; i < oprs.length; i++)
			oprnd[i] = oprs[i];
	}

	public static final int nop = 0;
	public static final int mult = 1;
	public static final int div = 2;
	public static final int multI = 3;
	public static final int divI = 4;
	public static final int rdivI = 5;
	public static final int add = 6;
	public static final int sub = 7;
	public static final int addI = 8;
	public static final int subI = 9;
	public static final int rsubI = 10;
	public static final int lshift = 11;
	public static final int lshiftI = 12;
	public static final int rshift = 13;
	public static final int rshiftI = 14;
	public static final int and = 15;
	public static final int andI = 16;
	public static final int or = 17;
	public static final int orI = 18;
	public static final int xor = 19;
	public static final int xorI = 20;
	public static final int load = 21;
	public static final int loadI = 22;
	public static final int loadAI = 23;
	public static final int loadAO = 24;
	public static final int cload = 25;
	public static final int cloadAI = 26;
	public static final int cloadAO = 27;
	public static final int store = 28;
	public static final int storeAI = 29;
	public static final int storeAO = 30;
	public static final int cstore = 31;
	public static final int cstoreAI = 32;
	public static final int cstoreAO = 33;
	public static final int i2i = 34;
	public static final int c2c = 35;
	public static final int c2i = 36;
	public static final int i2c = 37;
	public static final int move = 38;
	public static final int jump = 39;
	public static final int jumpI = 40;
	public static final int cbr = 41;
	public static final int tbl = 42;
	public static final int cmp_LT = 43;
	public static final int cmp_LE = 44;
	public static final int cmp_EQ = 45;
	public static final int cmp_NE = 46;
	public static final int cmp_GT = 47;
	public static final int cmp_GE = 48;
	public static final int comp = 49;
	public static final int cbr_LT = 50;
	public static final int cbr_LE = 51;
	public static final int cbr_EQ = 52;
	public static final int cbr_NE = 52;
	public static final int cbr_GT = 53;
	public static final int cbr_GE = 54;
	public static final int phi = 55;
}
