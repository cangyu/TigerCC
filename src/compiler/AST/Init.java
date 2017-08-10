package compiler.AST;

import java.util.*;

public class Init extends ASTNode
{
	public boolean listed;
	public AssignExp exp;
	public LinkedList<Init> init_list;

	public Init(AssignExp e)
	{
		listed = false;
		exp = e;
	}

	public Init()
	{
		listed = true;
		init_list = new LinkedList<Init>();
	}

	public void add_init(Init x)
	{
		init_list.add(x);
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
