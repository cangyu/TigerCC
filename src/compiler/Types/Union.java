package compiler.Types;

import java.lang.Math;

public final class Union extends Record
{
	public Union()
	{
		super();
	}

	public void add_record(Type t, String n)
	{
		fields.add(new RecordField(t, n));
		width = Math.max(width, t.width);
	}
}
