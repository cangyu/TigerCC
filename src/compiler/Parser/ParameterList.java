package compiler.Parser;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class ParameterList extends ASTNode
{
    public PlainDeclaration head;
    public ParameterList next;

    public ParameterList(PlainDeclaration _pd, ParameterList _n)
    {
        head = _pd;
        next = _n;
    }

    public void accept(ASTNodeVisitor v) throws Exception
    {
        v.visit(this);
    }
}
