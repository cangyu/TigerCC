package compiler.AST;

import compiler.Lexer.Token;

public class AssignExp extends Exp
{
	public static final int ASSIGN = 0;
	public static final int MUL_ASSIGN = 1;
	public static final int DIV_ASSIGN = 2;
	public static final int MOD_ASSIGN = 3;
	public static final int ADD_ASSIGN = 4;
	public static final int SUB_ASSIGN = 5;
	public static final int SHL_ASSIGN = 6;
	public static final int SHR_ASSIGN = 7;
	public static final int AND_ASSIGN = 8;
	public static final int XOR_ASSIGN = 9;
	public static final int OR_ASSIGN = 10;

	public int assign_type;
	public UnaryExp left;
	public Exp right;

	public AssignExp()
	{
		super();
		assign_type = -1;
		left = null;
		right = null;
	}

	public String assign_symbol()
	{
		switch (assign_type)
		{
		case ASSIGN:
			return Token.raw_rep(Token.ASSIGN);
		case MUL_ASSIGN:
			return Token.raw_rep(Token.MUL_ASSIGN);
		case DIV_ASSIGN:
			return Token.raw_rep(Token.DIV_ASSIGN);
		case MOD_ASSIGN:
			return Token.raw_rep(Token.MOD_ASSIGN);
		case ADD_ASSIGN:
			return Token.raw_rep(Token.ADD_ASSIGN);
		case SUB_ASSIGN:
			return Token.raw_rep(Token.SUB_ASSIGN);
		case SHL_ASSIGN:
			return Token.raw_rep(Token.SHL_ASSIGN);
		case SHR_ASSIGN:
			return Token.raw_rep(Token.SHR_ASSIGN);
		case AND_ASSIGN:
			return Token.raw_rep(Token.AND_ASSIGN);
		case XOR_ASSIGN:
			return Token.raw_rep(Token.XOR_ASSIGN);
		case OR_ASSIGN:
			return Token.raw_rep(Token.OR_ASSIGN);
		default:
			return "".intern();
		}
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
