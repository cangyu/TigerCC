package compiler.Parser;

import java.util.*;

public class TypeSpecifier
{
	public final static int ts_void = 0;
	public final static int ts_int = 1;
	public final static int ts_char = 2;
	public final static int ts_float = 3;
	public final static int ts_double = 4;
	public final static int ts_struct = 5;
	public final static int ts_union = 6;

	public int ts_type;
	public String name;
	public LinkedList<RecordEntry> entry;

	public TypeSpecifier(int t)
	{
		ts_type = t;
		if (t == ts_struct || t == ts_union)
			entry = new LinkedList<RecordEntry>();
	}

	public TypeSpecifier(int t, String n)
	{
		ts_type = t;
		name = n;
		if (t == ts_struct || t == ts_union)
			entry = new LinkedList<RecordEntry>();
	}

	public void add_entry(RecordEntry x)
	{
		entry.add(x);
	}
}
