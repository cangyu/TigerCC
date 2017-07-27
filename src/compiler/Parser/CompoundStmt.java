package compiler.Parser;

import compiler.AST.ASTNodeVisitor;
import java.util.*;

public class CompoundStmt extends Stmt
{
	public LinkedList<Declaration> decls;
	public LinkedList<Stmt> stmts;

	public CompoundStmt()
	{
		decls = new LinkedList<Declaration>();
		stmts = new LinkedList<Stmt>();
	}

	public void add_decl(Declaration x)
	{
		decls.add(x);
	}

	public void add_stmt(Stmt x)
	{
		stmts.add(x);
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
