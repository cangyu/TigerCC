package compiler.AST;

import compiler.Lexer.Token;

public class AssignExp extends Exp
{
	public int assign_type;
	public Exp left, right;

	public AssignExp()
	{
		super();
		assign_type = -1;
		left = null;
		right = null;
	}

	public String get_op()
	{
		switch (assign_type)
		{
		case plain:
			return Token.raw_rep(Token.ASSIGN);
		case multi:
			return Token.raw_rep(Token.MUL_ASSIGN);
		case divide:
			return Token.raw_rep(Token.DIV_ASSIGN);
		case module:
			return Token.raw_rep(Token.MOD_ASSIGN);
		case add:
			return Token.raw_rep(Token.ADD_ASSIGN);
		case sub:
			return Token.raw_rep(Token.SUB_ASSIGN);
		case left_shift:
			return Token.raw_rep(Token.SHL_ASSIGN);
		case right_shift:
			return Token.raw_rep(Token.SHR_ASSIGN);
		case bit_and:
			return Token.raw_rep(Token.AND_ASSIGN);
		case bit_xor:
			return Token.raw_rep(Token.XOR_ASSIGN);
		case bit_or:
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

	public static final int plain = 0;
	public static final int multi = 1;
	public static final int divide = 2;
	public static final int module = 3;
	public static final int add = 4;
	public static final int sub = 5;
	public static final int left_shift = 6;
	public static final int right_shift = 7;
	public static final int bit_and = 8;
	public static final int bit_xor = 9;
	public static final int bit_or = 10;
}
