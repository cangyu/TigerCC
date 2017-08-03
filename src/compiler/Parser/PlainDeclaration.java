package compiler.Parser;

public class PlainDeclaration
{
	public TypeSpecifier ts;
	public Declarator dlr;

	public PlainDeclaration(TypeSpecifier _ts, Declarator _d)
	{
		ts = _ts;
		dlr = _d;
	}
}
