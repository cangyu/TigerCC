package compiler.Syntactic;

public class Str extends Token
{
	public final String lexeme;
	
	public Str(String x)
	{
		super(Tag.STR);
		lexeme = x.intern();
	}
	
	public String toString()
	{
		return lexeme;
	}
}
