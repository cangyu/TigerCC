package compiler.AST;

import compiler.Types.Type;

public class UnaryExp extends Exp
{
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
}
