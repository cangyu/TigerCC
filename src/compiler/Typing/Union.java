package compiler.Typing;

import java.lang.Math;

public final class Union extends Record
{
	public Union()
	{
		super();
	}

	public void add_record(String n, Type t)
	{
		add_member(n, t);
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

    
	@Override
    public String toString()
    {
        return String.format("union %s ", tag);
    }
}
