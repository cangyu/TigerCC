package compiler.Types;

public final class Function extends Type
{
	Type[] arg_type;
	Type ret_type;

	public Function(Type ret, Type ...arg)
	{
		super(4);
		ret_type = ret;
		arg_type = new Type[arg.length];
		for(int i=0;i<arg.length;i++)
			arg_type[i] = arg[i];
	}
}
