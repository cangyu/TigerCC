package compiler.Parser;

import java.util.*;

public class Expression
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
}
