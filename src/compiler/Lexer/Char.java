package compiler.Lexer;

public class Char extends Token
{
	public final Character ch;

	public Char(Character x, int l, int c)
	{
		super(Tag.CH, l, c);
		ch = x;
	}

	public String toString()
	{
		return prefix + ch.toString();
	}

	private static final String prefix = "Char Literal: ".intern();
}
