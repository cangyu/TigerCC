package compiler.Lexer;

public class Str extends Token
{
	public final String lexeme;

	public Str(String x, int l, int c)
	{
		super(STR, l, c);
		lexeme = x.intern();
	}

	public String toString()
	{
		return prefix + lexeme;
	}

	private static final String prefix = "String Literal: ".intern();
}
