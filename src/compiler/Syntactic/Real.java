package compiler.Syntactic;

public class Real extends Token
{
	public final double value;
	
	public Real(double x)
	{
		super(Tag.REAL);
		value = x;
	}
	
	public String toString()
	{
		return Double.toString(value);
	}
}
