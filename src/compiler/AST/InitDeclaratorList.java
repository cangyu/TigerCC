package compiler.AST;

import java.util.*;

public class InitDeclaratorList extends ASTNode
{
	public LinkedList<InitDeclarator> comp;

	public InitDeclaratorList()
	{
		comp = new LinkedList<InitDeclarator>();
	}

	public void add_elem(InitDeclarator x)
	{
		comp.add(x);
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
