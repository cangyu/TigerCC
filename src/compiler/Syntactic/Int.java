package compiler.Syntactic;

public class Int extends Token
{
	public final int value;
	
	public Int(int x)
	{
		super(Tag.NUM);
		value = x;
	}
	
	public String toString()
	{
		return String.valueOf(value);
	}
}
