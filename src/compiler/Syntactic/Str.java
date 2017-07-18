package compiler.Syntactic;

public class Str extends Token
{
	public final String lexeme;
	private static final String prefix = "String Literal: ".intern();
	
	public Str(String x)
	{
		super(Tag.STR);
		lexeme = x.intern();
	}
	
	public String toString()
	{
		return prefix + lexeme;
	}
}
