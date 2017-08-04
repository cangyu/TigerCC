package compiler.AST;

import java.util.*;
import compiler.SymbolTable.*;

public class Prog extends ASTNode
{
	private LinkedList<Dec> decls;
	Env tenv, venv;

	public Prog()
	{
		decls = new LinkedList<Dec>();
		tenv = new Env(null);
		venv = new Env(null);
	}

	public Prog(LinkedList<Dec> dl, Env tv, Env vv)
	{
		decls = dl;
		tenv = tv;
		venv = vv;
	}

	public void add_dec(Dec d)
	{
		decls.add(d);
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		// TODO Auto-generated method stub

	}
}
