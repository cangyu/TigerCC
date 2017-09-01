package compiler.AST;

import compiler.Lexer.Token;

public class BinaryExp extends Exp
{
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

	public void calc_const_val() throws Exception
	{
		Object lhs = left.value;
		Object rhs = right.value;

		if (!(lhs instanceof Character || lhs instanceof Integer || lhs instanceof Float))
			throw new Exception("Invalid left operand.");
		if (!(rhs instanceof Character || rhs instanceof Integer || rhs instanceof Float))
			throw new Exception("Invalid right operand.");

		if (op == bitwise_and || op == bitwise_xor || op == bitwise_or)
		{
			int val = -1;
			int lval = lhs instanceof Character ? ((Character) lhs).charValue() : ((Integer) lhs).intValue();
			int rval = rhs instanceof Character ? ((Character) rhs).charValue() : ((Integer) rhs).intValue();

			if (op == bitwise_and)
				val = lval & rval;
			else if (op == bitwise_xor)
				val = lval ^ rval;
			else
				val = lval | rval;

			set_value(new Integer(val));
		}
		else if (op == logical_and || op == logical_or)
		{
			boolean lval = equals_zero(lhs);
			boolean rval = equals_zero(rhs);

			int val = -1;
			if (op == logical_and)
				val = lval && rval ? 1 : 0;
			else
				val = lval || rval ? 1 : 0;

			set_value(new Integer(val));
		}
		else if (op == equal || op == not_equal || op == less_than || op == greater_than || op == less_equal || op == greater_equal)
		{
			int val = -1;
			if (lhs instanceof Character || lhs instanceof Integer)
			{
				int lval = lhs instanceof Character ? ((Character) lhs).charValue() : ((Integer) lhs).intValue();
				if (rhs instanceof Character || rhs instanceof Integer)
				{
					int rval = rhs instanceof Character ? ((Character) rhs).charValue() : ((Integer) rhs).intValue();
					if (op == equal || op == not_equal)
						val = op == equal ? (lval == rval ? 1 : 0) : (lval != rval ? 1 : 0);
					else if (op == less_than || op == greater_than)
						val = op == less_than ? (lval < rval ? 1 : 0) : (lval > rval ? 1 : 0);
					else
						val = op == less_equal ? (lval <= rval ? 1 : 0) : (lval >= rval ? 1 : 0);
				}
				else
				{
					double rval = ((Double) rhs).doubleValue();
					if (op == equal || op == not_equal)
						val = op == equal ? (lval == rval ? 1 : 0) : (lval != rval ? 1 : 0);
					else if (op == less_than || op == greater_than)
						val = op == less_than ? (lval < rval ? 1 : 0) : (lval > rval ? 1 : 0);
					else
						val = op == less_equal ? (lval <= rval ? 1 : 0) : (lval >= rval ? 1 : 0);
				}
			}
			else
			{
				double lval = ((Double) lhs).doubleValue();
				if (rhs instanceof Character || rhs instanceof Integer)
				{
					int rval = rhs instanceof Character ? ((Character) rhs).charValue() : ((Integer) rhs).intValue();
					if (op == equal || op == not_equal)
						val = op == equal ? (lval == rval ? 1 : 0) : (lval != rval ? 1 : 0);
					else if (op == less_than || op == greater_than)
						val = op == less_than ? (lval < rval ? 1 : 0) : (lval > rval ? 1 : 0);
					else
						val = op == less_equal ? (lval <= rval ? 1 : 0) : (lval >= rval ? 1 : 0);
				}
				else
				{
					double rval = ((Double) rhs).doubleValue();
					if (op == equal || op == not_equal)
						val = op == equal ? (lval == rval ? 1 : 0) : (lval != rval ? 1 : 0);
					else if (op == less_than || op == greater_than)
						val = op == less_than ? (lval < rval ? 1 : 0) : (lval > rval ? 1 : 0);
					else
						val = op == less_equal ? (lval <= rval ? 1 : 0) : (lval >= rval ? 1 : 0);
				}
			}

			set_value(new Integer(val));
		}
		else if (op == shift_left || op == shift_right)
		{
			if (lhs instanceof Double)
				throw new Exception("FP value can't be shifted.");
			if (lhs instanceof Double)
				throw new Exception("Invalid right shift operand.");

			int val = -1;
			int lval = lhs instanceof Character ? ((Character) lhs).charValue() : ((Integer) lhs).intValue();
			int rval = rhs instanceof Character ? ((Character) rhs).charValue() : ((Integer) rhs).intValue();
			if (op == shift_left)
				val = lval << rval;
			else
				val = lval >> rval;

			set_value(new Integer(val));
		}
		else if (op == addition || op == substraction || op == multiply || op == division)
		{
			if (lhs instanceof Character || lhs instanceof Integer)
			{
				int lval = lhs instanceof Character ? ((Character) lhs).charValue() : ((Integer) lhs).intValue();
				if (rhs instanceof Character || rhs instanceof Integer)
				{
					int val = -1;
					int rval = rhs instanceof Character ? ((Character) rhs).charValue() : ((Integer) rhs).intValue();

					if (op == addition)
						val = lval + rval;
					else if (op == substraction)
						val = lval - rval;
					else
						val = lval * rval;

					set_value(new Integer(val));
				}
				else
				{
					double val = -1.0;
					double rval = ((Double) rhs).doubleValue();

					if (op == addition)
						val = lval + rval;
					else if (op == substraction)
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

					if (op == addition)
						val = lval + rval;
					else if (op == substraction)
						val = lval - rval;
					else
						val = lval * rval;

					set_value(new Double(val));
				}
				else if (rhs instanceof Double)
				{
					double rval = ((Double) rhs).doubleValue();

					if (op == addition)
						val = lval + rval;
					else if (op == substraction)
						val = lval - rval;
					else
						val = lval * rval;

					set_value(new Double(val));
				}
			}
		}
		else if (op == division)
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
		else if (op == module)
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

	public String get_op()
	{
		switch (op)
		{
		case bitwise_and:
			return Token.raw_rep(Token.BIT_AND);
		case bitwise_xor:
			return Token.raw_rep(Token.BIT_XOR);
		case bitwise_or:
			return Token.raw_rep(Token.BIT_OR);
		case logical_and:
			return Token.raw_rep(Token.AND);
		case logical_or:
			return Token.raw_rep(Token.OR);
		case equal:
			return Token.raw_rep(Token.EQ);
		case not_equal:
			return Token.raw_rep(Token.NE);
		case less_than:
			return Token.raw_rep(Token.LT);
		case greater_than:
			return Token.raw_rep(Token.GT);
		case less_equal:
			return Token.raw_rep(Token.LE);
		case greater_equal:
			return Token.raw_rep(Token.GE);
		case shift_left:
			return Token.raw_rep(Token.SHL);
		case shift_right:
			return Token.raw_rep(Token.SHR);
		case addition:
			return Token.raw_rep(Token.PLUS);
		case substraction:
			return Token.raw_rep(Token.MINUS);
		case multiply:
			return Token.raw_rep(Token.TIMES);
		case division:
			return Token.raw_rep(Token.DIVIDE);
		case module:
			return Token.raw_rep(Token.MODULE);
		default:
			return "".intern();
		}
	}

	public static final int bitwise_and = 0;
	public static final int bitwise_xor = 1;
	public static final int bitwise_or = 2;
	public static final int logical_and = 3;
	public static final int logical_or = 4;
	public static final int equal = 5;
	public static final int not_equal = 6;
	public static final int less_than = 7;
	public static final int greater_than = 8;
	public static final int less_equal = 9;
	public static final int greater_equal = 10;
	public static final int shift_left = 11;
	public static final int shift_right = 12;
	public static final int addition = 13;
	public static final int substraction = 14;
	public static final int multiply = 15;
	public static final int division = 16;
	public static final int module = 17;

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
}
