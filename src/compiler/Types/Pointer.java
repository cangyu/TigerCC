package compiler.Types;

public class Pointer extends Type
{
	Type elem_type;

	public Pointer(Type t)
	{
		super(4);
		elem_type = t;
	}
}