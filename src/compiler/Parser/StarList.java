package compiler.Parser;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class StarList extends ASTNode
{
	public int cnt;
	
	public StarList(int _ic)
	{
		cnt = _ic;
	}
	
    public void accept(ASTNodeVisitor v) throws Exception
    {
        v.visit(this);
    }
}
