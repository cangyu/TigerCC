package compiler.Parser;

import java.util.*;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class RecordEntry extends ASTNode
{
	public TypeSpecifier ts;
	public LinkedList<Declarator> dls;

	public RecordEntry(TypeSpecifier x)
	{
		ts = x;
		dls = new LinkedList<Declarator>();
	}

	public void add_elem(Declarator x)
	{
		dls.add(x);
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}