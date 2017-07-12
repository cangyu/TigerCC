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
		case ADD_ASSIGN:
			return "+=";
		case AND:
			return "&&";
		case AND_ASSIGN:
			return "&=";
		case ASSIGN:
			return "=";
		case BIT_AND:
			return "&";
		case BIT_NOT:
			return "~";
		case BIT_OR:
			return "|";
		case BIT_XOR:
			return "^";
		case BREAK:
			return "break";
		case CHAR:
			return "char";
		case COMMA:
			return ",";
		case CONTINUE:
			return "continue";
		case DEC:
			return "--";
		case DIVIDE:
			return "/";
		case DIV_ASSIGN:
			return "/=";
		case DOT:
			return ".";
		case DOUBLE:
			return "double";
		case ELSE:
			return "else";
		case EQ:
			return "==";
		case FLOAT:
			return "float";
		case FOR:
			return "for";
		case GE:
			return ">=";
		case GT:
			return ">";
		case IF:
			return "if";
		case INC:
			return "++";
		case INT:
			return "int";
		case LBRACE:
			return "{";
		case LE:
			return "<=";
		case LMPAREN:
			return "[";
		case LPAREN:
			return "(";
		case LT:
			return "<";
		case MINUS:
			return "-";
		case MODULE:
			return "%";
		case MOD_ASSIGN:
			return "%=";
		case MUL_ASSIGN:
			return "*=";
		case NE:
			return "!=";
		case NOT:
			return "!";
		case OR:
			return "||";
		case OR_ASSIGN:
			return "|=";
		case PLUS:
			return "+";
		case PTR:
			return "->";
		case RBRACE:
			return "}";
		case RETURN:
			return "return";
		case RMPAREN:
			return "]";
		case RPAREN:
			return ")";
		case SEMI:
			return ";";
		case SHL:
			return "<<";
		case SHL_ASSIGN:
			return "<<=";
		case SHR:
			return ">>";
		case SHR_ASSIGN:
			return ">>=";
		case SIZEOF:
			return "sizeof";
		case STRUCT:
			return "struct";
		case SUB_ASSIGN:
			return "-=";
		case TIMES:
			return "*";
		case TYPEDEF:
			return "typedef";
		case UNION:
			return "union";
		case VOID:
			return "void";
		case WHILE:
			return "while";
		case XOR_ASSIGN:
			return "^=";
		default:
			return "";
		}
	}
}
