package compiler.Symbols;

public abstract class Record extends Type
{
	public String tag;

	public Record(String t)
	{
		super(0);
		tag = t;
	}
}
