package compiler.Lexer;

public class Token
{
	public static final int EOF = -255;
	public static final int ID = -254;
	public static final int NUM = -253;
	public static final int REAL = -252;
	public static final int STR = -251;
	public static final int CH = -250;
	public static final int LINECOMMENT = -249;
	public static final int BLKCOMMENT = -248;
	public static final int VOID = 0;
	public static final int CHAR = 1;
	public static final int INT = 2;
	public static final int FLOAT = 3;
	public static final int DOUBLE = 4;
	public static final int STRUCT = 5;
	public static final int UNION = 6;
	public static final int IF = 7;
	public static final int ELSE = 8;
	public static final int WHILE = 9;
	public static final int FOR = 10;
	public static final int CONTINUE = 11;
	public static final int BREAK = 12;
	public static final int RETURN = 13;
	public static final int SIZEOF = 14;
	public static final int TYPEDEF = 15;
	public static final int LPAREN = 16;
	public static final int RPAREN = 17;
	public static final int LBRACE = 18;
	public static final int RBRACE = 19;
	public static final int LMPAREN = 20;
	public static final int RMPAREN = 21;
	public static final int SEMI = 22;
	public static final int COMMA = 23;
	public static final int PLUS = 24;
	public static final int MINUS = 25;
	public static final int TIMES = 26;
	public static final int DIVIDE = 27;
	public static final int MODULE = 28;
	public static final int INC = 29;
	public static final int DEC = 30;
	public static final int ASSIGN = 31;
	public static final int MUL_ASSIGN = 32;
	public static final int DIV_ASSIGN = 33;
	public static final int MOD_ASSIGN = 34;
	public static final int ADD_ASSIGN = 35;
	public static final int SUB_ASSIGN = 36;
	public static final int SHL_ASSIGN = 37;
	public static final int SHR_ASSIGN = 38;
	public static final int AND_ASSIGN = 39;
	public static final int XOR_ASSIGN = 40;
	public static final int OR_ASSIGN = 41;
	public static final int NOT = 42;
	public static final int OR = 43;
	public static final int AND = 44;
	public static final int DOT = 45;
	public static final int PTR = 46;
	public static final int EQ = 47;
	public static final int NE = 48;
	public static final int LT = 49;
	public static final int LE = 50;
	public static final int GT = 51;
	public static final int GE = 52;
	public static final int BIT_OR = 53;
	public static final int BIT_XOR = 54;
	public static final int BIT_AND = 55;
	public static final int BIT_NOT = 56;
	public static final int SHL = 57;
	public static final int SHR = 58;

	public final int tag;
	public int line, column;

	public Token(int x, int l, int c)
	{
		tag = x;
		line = l;
		column = c;
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
		case LINECOMMENT:
		case BLKCOMMENT:
			return "COMMENT".intern();
		default:
			return "".intern();
		}
	}

	public static String raw_rep(int t)
	{
		switch (t)
		{
		case EOF:
			return "eof".intern();
		case VOID:
			return "void".intern();
		case INT:
			return "int".intern();
		case CHAR:
			return "char".intern();
		case FLOAT:
			return "float".intern();
		case DOUBLE:
			return "double".intern();
		case STRUCT:
			return "struct".intern();
		case UNION:
			return "union".intern();
		case TYPEDEF:
			return "typedef".intern();
		case SIZEOF:
			return "sizeof".intern();
		case FOR:
			return "for".intern();
		case WHILE:
			return "while".intern();
		case IF:
			return "if".intern();
		case ELSE:
			return "else".intern();
		case BREAK:
			return "break".intern();
		case CONTINUE:
			return "continue".intern();
		case RETURN:
			return "return".intern();
		case PTR:
			return "->".intern();
		case DOT:
			return ".".intern();
		case INC:
			return "++".intern();
		case DEC:
			return "--".intern();
		case TIMES:
			return "*".intern();
		case DIVIDE:
			return "/".intern();
		case MODULE:
			return "%".intern();
		case PLUS:
			return "+".intern();
		case MINUS:
			return "-".intern();
		case SHL:
			return "<<".intern();
		case SHR:
			return ">>".intern();
		case GE:
			return ">=".intern();
		case GT:
			return ">".intern();
		case LE:
			return "<=".intern();
		case LT:
			return "<".intern();
		case EQ:
			return "==".intern();
		case NE:
			return "!=".intern();
		case BIT_AND:
			return "&".intern();
		case BIT_NOT:
			return "~".intern();
		case BIT_OR:
			return "|".intern();
		case BIT_XOR:
			return "^".intern();
		case AND:
			return "&&".intern();
		case OR:
			return "||".intern();
		case NOT:
			return "!".intern();
		case ASSIGN:
			return "=".intern();
		case ADD_ASSIGN:
			return "+=".intern();
		case AND_ASSIGN:
			return "&=".intern();
		case DIV_ASSIGN:
			return "/=".intern();
		case SHL_ASSIGN:
			return "<<=".intern();
		case SHR_ASSIGN:
			return ">>=".intern();
		case MOD_ASSIGN:
			return "%=".intern();
		case MUL_ASSIGN:
			return "*=".intern();
		case SUB_ASSIGN:
			return "-=".intern();
		case XOR_ASSIGN:
			return "^=".intern();
		case OR_ASSIGN:
			return "|=".intern();
		case LBRACE:
			return "{".intern();
		case LMPAREN:
			return "[".intern();
		case LPAREN:
			return "(".intern();
		case RBRACE:
			return "}".intern();
		case RMPAREN:
			return "]".intern();
		case RPAREN:
			return ")".intern();
		case COMMA:
			return ",".intern();
		case SEMI:
			return ";".intern();
		case LINECOMMENT:
			return "//...".intern();
		case BLKCOMMENT:
			return "/* ... */".intern();
		default:
			return "".intern();
		}
	}
}
