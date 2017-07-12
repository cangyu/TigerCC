package compiler.Syntactic;

public class Char extends Token
{
	public final Character ch;
	
	public Char(Character x)
	{
		super(Tag.CH);
		ch = x;
	}
	
	public String toString()
	{
		return ch.toString();
	}
}
