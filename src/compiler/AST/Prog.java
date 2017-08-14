package compiler.AST;

import java.util.*;
import compiler.Scoping.*;

public class Prog extends ASTNode
{
	public LinkedList<Dec> global_decl;
	public Env tenv, venv;

	public Prog()
	{
		global_decl = new LinkedList<Dec>();
		tenv = new Env(null);
		venv = new Env(null);
	}

	public void add_dec(Dec d)
	{
		global_decl.add(d);
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
