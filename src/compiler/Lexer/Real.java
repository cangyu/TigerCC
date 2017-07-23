package compiler.Lexer;

public class Real extends Token
{
	public final double value;

	public Real(double x, int l, int c)
	{
		super(Tag.REAL, l, c);
		value = x;
	}

	public String toString()
	{
		return prefix + Double.toString(value);
	}

	private static final String prefix = "Real: ".intern();
}
