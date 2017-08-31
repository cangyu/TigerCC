package compiler.AST;

import java.util.*;
import compiler.Scoping.Symbol;
import compiler.Typing.*;
import compiler.Typing.Void;

public class Init extends ASTNode
{
	public boolean listed;
	public Exp exp;
	public LinkedList<Init> init_list;

	public Init(Exp e)
	{
		listed = false;
		exp = e;
	}

	public Init()
	{
		listed = true;
		init_list = new LinkedList<Init>();
	}

	public void add_init(Init x)
	{
		init_list.add(x);
	}

	public boolean check_init(Type var_type)
	{
		if (var_type instanceof Array)
		{
			if (!listed)
				return false;

			ListIterator<Init> lit = init_list.listIterator();
			Type aet = ((Array) var_type).elem_type;
			while (lit.hasNext())
			{
				Init ci = lit.next();
				if (!ci.check_init(aet))
					return false;
			}
			return true;
		}
		else if (var_type instanceof Pointer)
		{
			if (listed)
				return false;

			return exp.type.isConvertableTo(var_type) || exp.type instanceof Array;
		}
		else if (var_type instanceof Function)
		{
			// In this version, function pointer is not supported
			return false;
		}
		else if (var_type instanceof Void)
		{
			// void-type variables are not writable
			return false;
		}
		else if (var_type instanceof Char)
		{
			if (listed)
				return false;

			return exp.type.isConvertableTo(var_type);
		}
		else if (var_type instanceof Int)
		{
			if (listed)
				return false;

			return exp.type.isConvertableTo(var_type);

		}
		else if (var_type instanceof FP)
		{
			if (listed)
				return false;

			return exp.type.isConvertableTo(var_type);
		}
		else if (var_type instanceof Struct)
		{
			if (!listed)
				return exp.type.isConvertableTo(var_type);
			else
			{
				// too much elements
				if (init_list.size() > ((Struct) var_type).field.size())
					return false;

				ListIterator<Init> ilit = init_list.listIterator();
				Iterator<Map.Entry<Symbol, Type>> rlit = ((Struct) var_type).field.entrySet().iterator();
				while (ilit.hasNext())
				{
					Init cit = ilit.next();
					Type ct = rlit.next().getValue();
					if (!cit.check_init(ct))
						return false;
				}
				return true;
			}
		}
		else if (var_type instanceof Union)
		{
			if (!listed)
				return exp.type.isConvertableTo(var_type);
			else
			{
				// Here, only allows simple initialization of a union
				if (init_list.size() != 1)
					return false;

				Init ci = init_list.getFirst();
				Type ct = ((Union) var_type).field.entrySet().iterator().next().getValue();
				return ci.check_init(ct);
			}
		}
		else
			return false;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
