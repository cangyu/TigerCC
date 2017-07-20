package compiler.Syntactic;

public class Str extends Token
{
	public final String lexeme;
	private static final String prefix = "String Literal: ".intern();

	public Str(String x, int l, int c)
	{
		super(Tag.STR, l, c);
		lexeme = x.intern();
	}

	public String toString()
	{
		return prefix + lexeme;
	}
}
