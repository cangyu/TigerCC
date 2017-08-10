package compiler.Typing;

import java.util.*;

public abstract class Record extends Type
{
	public String tag;
	public Hashtable<String, Type> field;

	public Record()
	{
		super(0);
		tag = null;
		field = new Hashtable<String, Type>();
	}

	public void set_tag(String tg)
	{
		tag = tg;
	}

	public Type get_member_type(String m)
	{
		return field.get(m);
	}
}
