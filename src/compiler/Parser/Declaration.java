package compiler.Parser;

import compiler.AST.ASTNodeVisitor;
import java.util.LinkedList;

public class Declaration extends ProgramComp
{
	public TypeSpecifier ts;
	public LinkedList<InitDeclarator> init;

	public Declaration(TypeSpecifier _ts)
	{
		ts = _ts;
		init = new LinkedList<InitDeclarator>();
	}

	public void add_init(InitDeclarator x)
	{
		init.add(x);
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
