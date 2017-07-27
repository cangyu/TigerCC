package compiler.Parser;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class PlainDeclarator extends ASTNode
{
	public int star_num;
	public String name;

	public PlainDeclarator(int sn, String id)
	{
		star_num = sn;
		name = id;
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
