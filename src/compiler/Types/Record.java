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

	public Record()
	{
		super(0);
		tag = null;
		fields = new LinkedList<RecordField>();
	}

	public void set_tag(String tg)
	{
		tag = tg;
	}
}
