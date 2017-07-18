package compiler.Syntactic;

public class Char extends Token
{
	public final Character ch;
	private static final String prefix = "Char Literal: ".intern();

	public Char(Character x)
	{
		super(Tag.CH);
		ch = x;
	}

	public String toString()
	{
		return prefix + ch.toString();
	}
}
