package compiler.Syntactic;

public enum Tag
{
	ID, NUM, REAL, STR, CH,
	VOID, CHAR, INT, FLOAT, DOUBLE,
	STRUCT, UNION, 
	IF, ELSE, 
	WHILE, FOR, 
	CONTINUE, BREAK, RETURN, 
	SIZEOF, TYPEDEF,
	LPAREN, RPAREN, LBRACE, RBRACE, LMPAREN, RMPAREN, 
	SEMI, COMMA, 
	PLUS, MINUS, TIMES, DIVIDE, MODULE, 
	INC, DEC, 
	ASSIGN, MUL_ASSIGN, DIV_ASSIGN, MOD_ASSIGN, ADD_ASSIGN, SUB_ASSIGN, SHL_ASSIGN, SHR_ASSIGN, AND_ASSIGN, XOR_ASSIGN, OR_ASSIGN, 
	NOT, OR, AND, 
	DOT, PTR, 
	EQ, NE, LT, LE, GT, GE, 
	BIT_OR, BIT_XOR, BIT_AND, BIT_NOT, 
	SHL, SHR;
}
