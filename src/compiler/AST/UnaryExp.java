package compiler.AST;

import compiler.Lexer.Token;
import compiler.Typing.Type;

public class UnaryExp extends Exp
{
	public static final int address_of = 0;
	public static final int indirection = 1;
	public static final int unary_plus = 2;
	public static final int unary_minus = 3;
	public static final int bitwise_not = 4;
	public static final int logical_negation = 5;
	public static final int size_of = 6;
	public static final int inc = 7;
	public static final int dec = 8;

	public int category;
	public Exp exp;
	public Type stp; // for sizeof '(' type-name ')'

	public UnaryExp(int c, Exp e, Type tp)
	{
		category = c;
		exp = e;
		stp = tp;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}

	public String op_symbol()
	{
		switch (category)
		{
		case address_of:
			return Token.raw_rep(Token.BIT_AND);
		case indirection:
			return Token.raw_rep(Token.TIMES);
		case unary_plus:
			return Token.raw_rep(Token.PLUS);
		case unary_minus:
			return Token.raw_rep(Token.MINUS);
		case bitwise_not:
			return Token.raw_rep(Token.BIT_NOT);
		case logical_negation:
			return Token.raw_rep(Token.NOT);
		case size_of:
			return Token.raw_rep(Token.SIZEOF);
		case inc:
			return Token.raw_rep(Token.INC);
		case dec:
			return Token.raw_rep(Token.DEC);
		default:
			return "".intern();
		}
	}
}
