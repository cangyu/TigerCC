package compiler.AST;

import compiler.SymbolTable.*;

public class CompStmt extends Stmt
{
	Env scope;

	public CompStmt()
	{

	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
