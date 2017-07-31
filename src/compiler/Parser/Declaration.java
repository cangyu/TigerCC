package compiler.Parser;

import compiler.AST.ASTNodeVisitor;
import java.util.LinkedList;

public class Declaration extends ProgramComp
{
	public TypeSpecifier ts;
	public LinkedList<InitDeclarator> elem;

	public Declaration(TypeSpecifier _ts)
	{
		ts = _ts;
		elem = new LinkedList<InitDeclarator>();
	}

	public void add_elem(InitDeclarator x)
	{
		elem.add(x);
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
