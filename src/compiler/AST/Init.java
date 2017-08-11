package compiler.AST;

import java.util.*;
import compiler.Typing.*;

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

	public Type get_type()
	{
		Type ret = null;

		return ret;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
