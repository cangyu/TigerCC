package compiler.Parser;

import compiler.AST.ASTNodeVisitor;
import java.util.*;

public class Expression extends Expr
{
	public LinkedList<AssignmentExpr> elem;

	public Expression()
	{
		elem = new LinkedList<AssignmentExpr>();
	}

	public void add_expr(AssignmentExpr x)
	{
		elem.add(x);
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
