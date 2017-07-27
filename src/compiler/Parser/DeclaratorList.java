package compiler.Parser;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class DeclaratorList extends ASTNode
{
    public Declarator head;
    public DeclaratorList next;

    public DeclaratorList(Declarator _d, DeclaratorList _n)
    {
        head = _d;
        next = _n;
    }

    public void accept(ASTNodeVisitor v) throws Exception
    {
        v.visit(this);
    }
}
