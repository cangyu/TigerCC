package compiler.Parser;

import java.util.*;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class InitDeclarators extends ASTNode
{
	public LinkedList<InitDeclarator> comp;

	public InitDeclarators()
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
