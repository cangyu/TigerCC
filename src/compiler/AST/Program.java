package compiler.AST;

import java.util.*;

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
