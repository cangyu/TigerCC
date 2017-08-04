package compiler.Types;

public final class Struct extends Record
{
	public Struct()
	{
		super();
	}

	public void add_record(Type t, String n)
	{
		fields.add(new RecordField(t, n));
		width += t.width;
	}
}
