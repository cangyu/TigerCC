package compiler.Parser;

import compiler.AST.ASTNodeVisitor;
import compiler.Lexer.*;

public class PrimaryExpr extends Expr
{
	public static final int identifier = 0;
	public static final int constant = 1;
	public static final int string = 2;
	public static final int paren_expr = 3;

	public int type;
	public Object elem;

	public PrimaryExpr(Object e) throws Exception
	{
		if (e instanceof Identifier)
		{
			type = identifier;
			Identifier t = (Identifier) e;
			elem = t.name;
		}
		else if (e instanceof Char)
		{
			type = constant;
			Char t = (Char) e;
			elem = t.value;
		}
		else if (e instanceof Int)
		{
			type = constant;
			Int t = (Int) e;
			elem = t.value;
		}
		else if (e instanceof Real)
		{
			type = constant;
			Real t = (Real) e;
			elem = t.value;
		}
		else if (e instanceof Str)
		{
			type = string;
			Str t = (Str) e;
			elem = t.lexeme;
		}
		else if (e instanceof Expression)
		{
			type = paren_expr;
			elem = e;
		}
		else
			throw new Exception("Invalid input object.");
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
