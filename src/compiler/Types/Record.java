package compiler.Types;

import java.util.*;

public abstract class Record extends Type
{
	class RecordField
	{
		Type type;
		String name;

		public RecordField(Type t, String n)
		{
			type = t;
			name = n;
		}
	}

	public String tag;
	public LinkedList<RecordField> fields;

	public Record(String t)
	{
		super(0);
		tag = t;
		fields = new LinkedList<RecordField>();
	}
	
	public Record()
	{
		super(0);
		tag = null;
		fields = new LinkedList<RecordField>();
	}

	public void add_record(Type t, String n)
	{
		fields.add(new RecordField(t, n));
		width += t.width;
	}
}
