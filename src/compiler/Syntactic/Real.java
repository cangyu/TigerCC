package compiler.Syntactic;

public class Real extends Token
{
	public final double value;
	private static final String prefix = "Real: ".intern();

	public Real(double x)
	{
		super(Tag.REAL);
		value = x;
	}

	public String toString()
	{
		return prefix + Double.toString(value);
	}
}
