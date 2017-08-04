package compiler.Parser;

import java.util.*;

public class CompoundStatement extends Statement
{
	public LinkedList<Declaration> decls;
	public LinkedList<Statement> stmts;

	public CompoundStatement()
	{
		decls = new LinkedList<Declaration>();
		stmts = new LinkedList<Statement>();
	}

	public void add_decl(Declaration x)
	{
		decls.add(x);
	}

	public void add_stmt(Statement x)
	{
		stmts.add(x);
	}
}
