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
			return "+=".intern();
		case AND:
			return "&&".intern();
		case AND_ASSIGN:
			return "&=".intern();
		case ASSIGN:
			return "=".intern();
		case BIT_AND:
			return "&".intern();
		case BIT_NOT:
			return "~".intern();
		case BIT_OR:
			return "|".intern();
		case BIT_XOR:
			return "^".intern();
		case BREAK:
			return "break".intern();
		case CHAR:
			return "char".intern();
		case COMMA:
			return ",".intern();
		case CONTINUE:
			return "continue".intern();
		case DEC:
			return "--".intern();
		case DIVIDE:
			return "/".intern();
		case DIV_ASSIGN:
			return "/=".intern();
		case DOT:
			return ".".intern();
		case DOUBLE:
			return "double".intern();
		case ELSE:
			return "else".intern();
		case EQ:
			return "==".intern();
		case FLOAT:
			return "float".intern();
		case FOR:
			return "for".intern();
		case GE:
			return ">=".intern();
		case GT:
			return ">".intern();
		case IF:
			return "if".intern();
		case INC:
			return "++".intern();
		case INT:
			return "int".intern();
		case LBRACE:
			return "{".intern();
		case LE:
			return "<=".intern();
		case LMPAREN:
			return "[".intern();
		case LPAREN:
			return "(".intern();
		case LT:
			return "<".intern();
		case MINUS:
			return "-".intern();
		case MODULE:
			return "%".intern();
		case MOD_ASSIGN:
			return "%=".intern();
		case MUL_ASSIGN:
			return "*=".intern();
		case NE:
			return "!=".intern();
		case NOT:
			return "!".intern();
		case OR:
			return "||".intern();
		case OR_ASSIGN:
			return "|=".intern();
		case PLUS:
			return "+".intern();
		case PTR:
			return "->".intern();
		case RBRACE:
			return "}".intern();
		case RETURN:
			return "return".intern();
		case RMPAREN:
			return "]".intern();
		case RPAREN:
			return ")".intern();
		case SEMI:
			return ".intern();".intern();
		case SHL:
			return "<<".intern();
		case SHL_ASSIGN:
			return "<<=".intern();
		case SHR:
			return ">>".intern();
		case SHR_ASSIGN:
			return ">>=".intern();
		case SIZEOF:
			return "sizeof".intern();
		case STRUCT:
			return "struct".intern();
		case SUB_ASSIGN:
			return "-=".intern();
		case TIMES:
			return "*".intern();
		case TYPEDEF:
			return "typedef".intern();
		case UNION:
			return "union".intern();
		case VOID:
			return "void".intern();
		case WHILE:
			return "while".intern();
		case XOR_ASSIGN:
			return "^=".intern();
		default:
			return "".intern();
		}
	}
}
