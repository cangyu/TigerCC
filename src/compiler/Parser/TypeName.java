package compiler.Parser;

public class TypeName
{
	public TypeSpecifier type_specifier;
	public int star_cnt;

	public TypeName(TypeSpecifier ts, int sc)
	{
		type_specifier = ts;
		star_cnt = sc;
	}
}
