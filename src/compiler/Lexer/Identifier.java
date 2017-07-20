package compiler.Lexer;

public class Identifier extends Token
{
	public final String name;
	private static final String prefix = "Identifier: ".intern();

	public Identifier(String x, int l, int c)
	{
		super(Tag.ID, l, c);
		name = x.intern();
	}

	public String toString()
	{
		return prefix + name;
	}
}
