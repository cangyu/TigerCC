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

	public BinaryExp(int _t)
	{
		op = _t;
	}

	public BinaryExp()
	{
		op = -1;// Undetermined
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}

	public String getOperator(int op)
	{
		switch (op)
		{
		case BIT_AND:
			return "&".intern();
		case BIT_XOR:
			return "^".intern();
		case BIT_OR:
			return "|".intern();
		case AND:
			return "&&".intern();
		case OR:
			return "||".intern();
		case EQ:
			return "==".intern();
		case NE:
			return "!=".intern();
		case LT:
			return "<".intern();
		case GT:
			return ">".intern();
		case LE:
			return "<=".intern();
		case GE:
			return ">=".intern();
		case SHL:
			return "<<".intern();
		case SHR:
			return ">>".intern();
		case PLUS:
			return "+".intern();
		case MINUS:
			return "-".intern();
		case TIMES:
			return "*".intern();
		case DIVIDE:
			return "/".intern();
		case MODULE:
			return "%".intern();
		default:
			return "".intern();
		}
	}
}
