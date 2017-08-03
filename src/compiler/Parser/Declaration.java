package compiler.Parser;

import java.util.LinkedList;

public class Declaration extends ProgComp
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
}
