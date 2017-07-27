package compiler.Parser;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;
import java.util.LinkedList;

public class Initializer extends ASTNode
{
	public static final int assign = 0;
	public static final int list = 1;

	public int type;
	public AssignmentExpr ae;
	public LinkedList<Initializer> comp;

	public Initializer(AssignmentExpr x)
	{
		type = assign;
		ae = x;
	}

	public Initializer()
	{
		type = list;
		comp = new LinkedList<Initializer>();
	}

	public void add_initializer(Initializer x)
	{
		comp.add(x);
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
