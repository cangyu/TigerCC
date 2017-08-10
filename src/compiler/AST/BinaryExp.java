package compiler.AST;

import compiler.Typing.Type;

public class BinaryExp extends Exp
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

	public BinaryExp()
	{
		super();
		op = -1;
		left = null;
		right = null;
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}

	public static boolean equals_zero(Object x) throws Exception
	{
		if (x instanceof Character)
			return (int) ((Character) x).charValue() == 0;
		else if (x instanceof Integer)
			return ((Integer) x).intValue() == 0;
		else if (x instanceof Double)
			return ((Double) x).doubleValue() == 0.0;
		else
			return false;
	}

	public void calc_const_val() throws Exception
	{
		Object lhs = left.value;
		Object rhs = right.value;

		if (!(lhs instanceof Character || lhs instanceof Integer || lhs instanceof Double))
			throw new Exception("Invalid left operand.");
		if (!(rhs instanceof Character || rhs instanceof Integer || rhs instanceof Double))
			throw new Exception("Invalid right operand.");

		if (op == BIT_AND || op == BIT_XOR || op == BIT_OR)
		{
			int val = -1;
			int lval = lhs instanceof Character ? ((Character) lhs).charValue() : ((Integer) lhs).intValue();
			int rval = rhs instanceof Character ? ((Character) rhs).charValue() : ((Integer) rhs).intValue();

			if (op == BIT_AND)
				val = lval & rval;
			else if (op == BIT_XOR)
				val = lval ^ rval;
			else
				val = lval | rval;

			set_value(new Integer(val));
		}
		else if (op == AND || op == OR)
		{
			boolean lval = equals_zero(lhs);
			boolean rval = equals_zero(rhs);

			int val = -1;
			if (op == AND)
				val = lval && rval ? 1 : 0;
			else
				val = lval || rval ? 1 : 0;

			set_value(new Integer(val));
		}
		else if (op == EQ || op == NE || op == LT || op == GT || op == LE || op == GE)
		{
			int val = -1;
			if (lhs instanceof Character || lhs instanceof Integer)
			{
				int lval = lhs instanceof Character ? ((Character) lhs).charValue() : ((Integer) lhs).intValue();
				if (rhs instanceof Character || rhs instanceof Integer)
				{
					int rval = rhs instanceof Character ? ((Character) rhs).charValue() : ((Integer) rhs).intValue();
					if (op == EQ || op == NE)
						val = op == EQ ? (lval == rval ? 1 : 0) : (lval != rval ? 1 : 0);
					else if (op == LT || op == GT)
						val = op == LT ? (lval < rval ? 1 : 0) : (lval > rval ? 1 : 0);
					else
						val = op == LE ? (lval <= rval ? 1 : 0) : (lval >= rval ? 1 : 0);
				}
				else
				{
					double rval = ((Double) rhs).doubleValue();
					if (op == EQ || op == NE)
						val = op == EQ ? (lval == rval ? 1 : 0) : (lval != rval ? 1 : 0);
					else if (op == LT || op == GT)
						val = op == LT ? (lval < rval ? 1 : 0) : (lval > rval ? 1 : 0);
					else
						val = op == LE ? (lval <= rval ? 1 : 0) : (lval >= rval ? 1 : 0);
				}
			}
			else
			{
				double lval = ((Double) lhs).doubleValue();
				if (rhs instanceof Character || rhs instanceof Integer)
				{
					int rval = rhs instanceof Character ? ((Character) rhs).charValue() : ((Integer) rhs).intValue();
					if (op == EQ || op == NE)
						val = op == EQ ? (lval == rval ? 1 : 0) : (lval != rval ? 1 : 0);
					else if (op == LT || op == GT)
						val = op == LT ? (lval < rval ? 1 : 0) : (lval > rval ? 1 : 0);
					else
						val = op == LE ? (lval <= rval ? 1 : 0) : (lval >= rval ? 1 : 0);
				}
				else
				{
					double rval = ((Double) rhs).doubleValue();
					if (op == EQ || op == NE)
						val = op == EQ ? (lval == rval ? 1 : 0) : (lval != rval ? 1 : 0);
					else if (op == LT || op == GT)
						val = op == LT ? (lval < rval ? 1 : 0) : (lval > rval ? 1 : 0);
					else
						val = op == LE ? (lval <= rval ? 1 : 0) : (lval >= rval ? 1 : 0);
				}
			}

			set_value(new Integer(val));
		}
		else if (op == SHL || op == SHR)
		{
			if (lhs instanceof Double)
				throw new Exception("FP value can't be shifted.");
			if (lhs instanceof Double)
				throw new Exception("Invalid right shift operand.");

			int val = -1;
			int lval = lhs instanceof Character ? ((Character) lhs).charValue() : ((Integer) lhs).intValue();
			int rval = rhs instanceof Character ? ((Character) rhs).charValue() : ((Integer) rhs).intValue();
			if (op == SHL)
				val = lval << rval;
			else
				val = lval >> rval;

			set_value(new Integer(val));
		}
		else if (op == PLUS || op == MINUS || op == TIMES || op == DIVIDE)
		{
			if (lhs instanceof Character || lhs instanceof Integer)
			{
				int lval = lhs instanceof Character ? ((Character) lhs).charValue() : ((Integer) lhs).intValue();
				if (rhs instanceof Character || rhs instanceof Integer)
				{
					int val = -1;
					int rval = rhs instanceof Character ? ((Character) rhs).charValue() : ((Integer) rhs).intValue();

					if (op == PLUS)
						val = lval + rval;
					else if (op == MINUS)
						val = lval - rval;
					else
						val = lval * rval;

					set_value(new Integer(val));
				}
				else
				{
					double val = -1.0;
					double rval = ((Double) rhs).doubleValue();

					if (op == PLUS)
						val = lval + rval;
					else if (op == MINUS)
						val = lval - rval;
					else
						val = lval * rval;

					set_value(new Double(val));
				}
			}
			else
			{
				double val = -1.0;
				double lval = ((Double) lhs).doubleValue();
				if (rhs instanceof Character || rhs instanceof Integer)
				{
					int rval = rhs instanceof Character ? ((Character) rhs).charValue() : ((Integer) rhs).intValue();

					if (op == PLUS)
						val = lval + rval;
					else if (op == MINUS)
						val = lval - rval;
					else
						val = lval * rval;

					set_value(new Double(val));
				}
				else if (rhs instanceof Double)
				{
					double rval = ((Double) rhs).doubleValue();

					if (op == PLUS)
						val = lval + rval;
					else if (op == MINUS)
						val = lval - rval;
					else
						val = lval * rval;

					set_value(new Double(val));
				}
			}
		}
		else if (op == DIVIDE)
		{
			if (equals_zero(rhs))
				throw new Exception("Dividend shall not be zero!");

			double lval = -1.0;
			double rval = -1.0;

			if (lhs instanceof Character)
				lval = (double) ((Character) lhs).charValue();
			else if (lhs instanceof Integer)
				lval = (double) ((Integer) lhs).intValue();
			else
				lval = ((Double) lhs).doubleValue();

			if (rhs instanceof Character)
				rval = (double) ((Character) rhs).charValue();
			else if (rhs instanceof Integer)
				rval = (double) ((Integer) rhs).intValue();
			else
				rval = ((Double) rhs).doubleValue();

			double val = lval / rval;
			set_value(new Double(val));
		}
		else if (op == MODULE)
		{
			if (equals_zero(rhs))
				throw new Exception("Right operand shall not be zero!");
			if (lhs instanceof Double)
				throw new Exception("Invalid left operand.");
			if (rhs instanceof Double)
				throw new Exception("Invalid right operand.");

			int lval = lhs instanceof Character ? ((Character) lhs).charValue() : ((Integer) lhs).intValue();
			int rval = rhs instanceof Character ? ((Character) rhs).charValue() : ((Integer) rhs).intValue();
			int val = lval % rval;

			// The result of a modulus operation's sign is implementation-defined.
			set_value(new Integer(val));
		}
		else
			throw new Exception("Internal Error.");
	}
}
