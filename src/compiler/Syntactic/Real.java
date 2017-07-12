package compiler.Syntactic;

public class Real extends Token
{
	public final float value;
	
	public Real(float x)
	{
		super(Tag.REAL);
		value = x;
	}
	
	public String toString()
	{
		return Float.toString(value);
	}
}
