package compiler.Syntactic;

public class Int extends Token
{
	public final int value;
	private static final String prefix = "Integer: ".intern();
	
	public Int(int x)
	{
		super(Tag.NUM);
		value = x;
	}
	
	public String toString()
	{
		return prefix + String.valueOf(value);
	}
}
