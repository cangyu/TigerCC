package compiler.Typing;

public class Pointer extends Type
{
	public Type elem_type;

	public Pointer(Type t)
	{
		super(4);
		elem_type = t;
		complete = true;
	}

	@Override
	public boolean equals(Type rhs)
	{
		return rhs instanceof Pointer;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return rhs instanceof Pointer || rhs instanceof Int;
	}

    @Override
    public String toString()
    {
        return String.format("ptr to %s", elem_type.toString());
    }
}
