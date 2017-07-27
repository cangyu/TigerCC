package compiler.Parser;

import java.util.*;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class Program extends ASTNode
{
	public LinkedList<ProgramComp> comp;

	public Program()
	{
		comp = new LinkedList<ProgramComp>();
	}

	public void add_elem(ProgramComp x)
	{
		comp.add(x);
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
