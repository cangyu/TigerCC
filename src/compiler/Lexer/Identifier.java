package compiler.Lexer;

public class Identifier extends Token
{
	public final String name;

	public Identifier(String x, int l, int c)
	{
		super(Tag.ID, l, c);
		name = x.intern();
	}

	public String toString()
	{
		return prefix + name;
	}

	private static final String prefix = "Identifier: ".intern();
}
