package compiler.Types;

public final class Function extends Type
{
	public Type returnType;
	public Type argumentType;

	public Function(Type arg, Type ret)
	{
		super(1);
		returnType = ret;
		argumentType = arg;
	}
}
