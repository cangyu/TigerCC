package compiler.Typing;

import java.util.*;

public final class Function extends Type
{
	public Type returnType;
	public Type argumentType;

	public Function(Type arg, Type ret)
	{
		super(1);
		returnType = ret;
		argumentType = arg;
		complete = true;
	}

	public static Type get_ret_type(Function func)
	{
		LinkedList<Type> tps = get_param_type(func);
		return tps.getLast();
	}

	public static LinkedList<Type> get_param_type(Function func)
	{
	    //the last element in returned value is the 'return_type'
		LinkedList<Type> ans = new LinkedList<Type>();
		Function f = func;
		for (;;)
		{
			Type arg = f.argumentType;
			Type ret = f.returnType;
			if (arg instanceof Void)
			{
				ans.add(ret);
				return ans;
			}
			else
			{
				ans.add(arg);
				f = (Function) ret;
			}
		}
	}

	@Override
	public boolean equals(Type rhs)
	{
		if (rhs instanceof Function)
		{
			LinkedList<Type> tp0 = get_param_type(this);
			LinkedList<Type> tp1 = get_param_type((Function) rhs);
			if (tp0.size() != tp1.size())
				return false;
			else
			{
				ListIterator<Type> lit0 = tp0.listIterator();
				ListIterator<Type> lit1 = tp1.listIterator();
				while (lit0.hasNext())
				{
					Type c0 = lit0.next();
					Type c1 = lit1.next();
					if (!c0.equals(c1))
						return false;
				}
				return true;
			}
		}
		else
			return false;
	}

	@Override
	public boolean isConvertableTo(Type rhs)
	{
		return equals(rhs);
	}

    @Override
    public String toString()
    {
        String ret = "Function: (".intern();
        LinkedList<Type> tps = get_param_type(this);
        ListIterator<Type> lit = tps.listIterator();
        
        ret += lit.next().toString();
        if(tps.size()>2)
        {
            while(lit.nextIndex()<tps.size()-2)
                ret += ", ".intern() +  lit.next().toString();
            
            //skip the last void
            lit.next();
        }
        
        ret += ") -> ".intern() + lit.next().toString();
        return ret;
    }
}
