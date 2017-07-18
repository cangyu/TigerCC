package compiler.Syntactic;

public class Identifier extends Token
{
	public final String name;
	private static final String prefix = "Identifier: ".intern();

	public Identifier(String x)
	{
		super(Tag.ID);
		name = x.intern();
	}

	public String toString()
	{
		return prefix + name;
	}
}
