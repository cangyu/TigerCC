package compiler.Syntactic;

public class Token
{
	public final Tag tag;

	public Token(Tag x)
	{
		tag = x;
	}

	public String toString()
	{
		switch (tag)
		{
		case EOF:
			return "EOF".intern();
		case VOID:
			return "VOID".intern();
		case INT:
			return "INT".intern();
		case CHAR:
			return "CHAR".intern();
		case FLOAT:
			return "FLOAT".intern();
		case DOUBLE:
			return "DOUBLE".intern();
		case STRUCT:
			return "STRUCT".intern();
		case UNION:
			return "UNION".intern();
		case TYPEDEF:
			return "TYPEDEF".intern();
		case SIZEOF:
			return "SIZEOF".intern();
		case FOR:
			return "FOR".intern();
		case WHILE:
			return "WHILE".intern();
		case IF:
			return "IF".intern();
		case ELSE:
			return "ELSE".intern();
		case BREAK:
			return "BREAK".intern();
		case CONTINUE:
			return "CONTINUE".intern();
		case RETURN:
			return "RETURN".intern();
		case PTR:
			return "PTR".intern();
		case DOT:
			return "DOT".intern();
		case INC:
			return "INC".intern();
		case DEC:
			return "DEC".intern();
		case TIMES:
			return "TIMES".intern();
		case DIVIDE:
			return "DIVIDE".intern();
		case MODULE:
			return "MODULE".intern();
		case PLUS:
			return "PLUS".intern();
		case MINUS:
			return "MINUS".intern();
		case SHL:
			return "SHL".intern();
		case SHR:
			return "SHR".intern();
		case GE:
			return "GE".intern();
		case GT:
			return "GT".intern();
		case LE:
			return "LE".intern();
		case LT:
			return "LT".intern();
		case EQ:
			return "EQ".intern();
		case NE:
			return "NE".intern();
		case BIT_AND:
			return "BIT_AND".intern();
		case BIT_NOT:
			return "BIT_NOT".intern();
		case BIT_OR:
			return "BIT_OR".intern();
		case BIT_XOR:
			return "BIT_XOR".intern();
		case AND:
			return "AND".intern();
		case OR:
			return "OR".intern();
		case NOT:
			return "NOT".intern();
		case ASSIGN:
			return "ASSIGN".intern();
		case ADD_ASSIGN:
			return "ADD_ASSIGN".intern();
		case AND_ASSIGN:
			return "AND_ASSIGN".intern();
		case DIV_ASSIGN:
			return "DIV_ASSIGN".intern();
		case SHL_ASSIGN:
			return "SHL_ASSIGN".intern();
		case SHR_ASSIGN:
			return "SHR_ASSIGN".intern();
		case MOD_ASSIGN:
			return "MOD_ASSIGN".intern();
		case MUL_ASSIGN:
			return "MUL_ASSIGN".intern();
		case SUB_ASSIGN:
			return "SUB_ASSIGN".intern();
		case XOR_ASSIGN:
			return "XOR_ASSIGN".intern();
		case OR_ASSIGN:
			return "OR_ASSIGN".intern();
		case LBRACE:
			return "LBRACE".intern();
		case LMPAREN:
			return "LMPAREN".intern();
		case LPAREN:
			return "LPAREN".intern();
		case RBRACE:
			return "RBRACE".intern();
		case RMPAREN:
			return "RMPAREN".intern();
		case RPAREN:
			return "RPAREN".intern();
		case COMMA:
			return "COMMA".intern();
		case SEMI:
			return "SEMI".intern();
		default:
			return "".intern(); // comments are omitted
		}
	}
}
