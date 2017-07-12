package compiler.Syntactic;

public class Identifier extends Token
{
	public final String name;
	
	public Identifier(String x)
	{
		super(Tag.ID);
		name = x.intern();
	}
	
	public String toString()
	{
		return name;
	}
}
