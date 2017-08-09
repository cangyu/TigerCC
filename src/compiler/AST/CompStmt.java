package compiler.AST;

import compiler.SymbolTable.*;
import java.util.*;

public class CompStmt extends Stmt
{
	public Env scope;
	public LinkedList<Stmt> st;

	public CompStmt()
	{
		scope = null;
		st = new LinkedList<Stmt>();
	}

	public void add_stmt(Stmt s)
	{
		st.add(s);
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
