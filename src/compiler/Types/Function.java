package compiler.Types;

public final class Function extends Type
{
	Type returnType;
	Type argumentType;

	public Function(Type arg, Type ret)
	{
		super(1);
		returnType = ret;
		argumentType = arg;
	}
}
