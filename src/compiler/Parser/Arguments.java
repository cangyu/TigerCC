package compiler.Parser;

import compiler.AST.*;
import java.util.*;

public class Arguments extends ASTNode
{
	public LinkedList<AssignmentExpr> elem;

	public Arguments()
	{
		elem = new LinkedList<AssignmentExpr>();
	}

	public void add_elem(AssignmentExpr x)
	{
		elem.add(x);
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
