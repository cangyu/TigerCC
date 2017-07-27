package compiler.Parser;

import compiler.AST.*;

public class TypeName extends ASTNode
{
	public TypeSpecifier type_specifier;
	public int star_cnt;

	public TypeName(TypeSpecifier ts, int sc)
	{
		type_specifier = ts;
		star_cnt = sc;
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
