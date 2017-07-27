package compiler.Parser;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class DeclarationList extends ASTNode
{
    public Declaration head;
    public DeclarationList next;

    public DeclarationList(Declaration _d, DeclarationList _n)
    {
        head = _d;
        next = _n;
    }

    public void accept(ASTNodeVisitor v) throws Exception
    {
        v.visit(this);
    }
}
