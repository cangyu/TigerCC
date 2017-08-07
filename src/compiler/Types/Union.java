package compiler.Types;

import java.lang.Math;
import java.util.*;

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

	@Override
	public boolean equals(Type rhs)
	{
		if (rhs == this)
			return true;

		if (rhs instanceof Union)
		{
			Union ur = (Union) rhs;

			// Firstly, check tag
			if (tag != null && ur.tag != null)
				return tag.equals(ur.tag);
			else if (tag == null && ur.tag == null)
				ur = ur;
			else
				return false;

			// Then, check fields
			if (fields.size() != ur.fields.size())
				return false;

			ListIterator<RecordField> lit0 = fields.listIterator();
			ListIterator<RecordField> lit1 = ur.fields.listIterator();
			while (lit0.hasNext())
			{
				RecordField rf0 = lit0.next();
				RecordField rf1 = lit1.next();
				if (!rf0.equals(rf1))
					return false;
			}
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return false;
	}
}
