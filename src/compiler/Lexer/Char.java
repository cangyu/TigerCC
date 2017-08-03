package compiler.Lexer;

public class Char extends Token
{
	public final Character value;

	public Char(Character x, int l, int c)
	{
		super(CH, l, c);
		value = x;
	}

	public String toString()
	{
		return prefix + value.toString();
	}

	private static final String prefix = "Char Literal: ".intern();
}
