package compiler.Parser;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class InitDeclarator extends ASTNode
{
	public Declarator declarator;
	public Initializer initializer;
	
	public InitDeclarator(Declarator _d, Initializer _i)
	{
		declarator = _d;
		initializer = _i;
	}
	
    public void accept(ASTNodeVisitor v) throws Exception
    {
        v.visit(this);
    }
}
