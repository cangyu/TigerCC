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
		field.put(n, t);
		width = Math.max(width, t.width);
	}

	@Override
	public boolean equals(Type rhs)
	{
		return rhs == this;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return false;
	}
}
