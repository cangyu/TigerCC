package compiler.Parser;

import compiler.AST.ASTNodeVisitor;

public class CompoundStmt extends Stmt
{
	public DeclarationList declaration_list;
	public StmtList stmt_list;
	
	public CompoundStmt(DeclarationList _ds, StmtList _ss)
	{
	    declaration_list = _ds;
	    stmt_list = _ss;
	}
	
    public void accept(ASTNodeVisitor v) throws Exception
    {
        v.visit(this);
    }
}
