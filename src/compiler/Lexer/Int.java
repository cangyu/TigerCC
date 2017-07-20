package compiler.Lexer;

public class Int extends Token
{
	public final int value;
	private static final String prefix = "Integer: ".intern();

	public Int(int x, int l, int c)
	{
		super(Tag.NUM, l, c);
		value = x;
	}

	public String toString()
	{
		return prefix + String.valueOf(value);
	}
}
