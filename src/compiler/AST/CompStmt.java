package compiler.AST;

import java.util.*;

import compiler.SymTbl.*;

public class CompStmt extends Stmt
{
	public Env scope;
	public LinkedList<VarDec> var;
	public LinkedList<Stmt> st;

	public CompStmt()
	{
		scope = null;
		var = new LinkedList<VarDec>();
		st = new LinkedList<Stmt>();
	}

	public void add_stmt(Stmt s)
	{
		st.add(s);
	}

	public void add_var(VarDec v)
	{
		var.add(v);
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
