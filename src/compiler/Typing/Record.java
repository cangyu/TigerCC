package compiler.Typing;

import java.util.*;

import compiler.Scoping.Symbol;

public abstract class Record extends Type
{
	public String tag;
	public LinkedHashMap<Symbol, Type> field;

	public Record()
	{
		super(0);
		tag = null;
		field = new LinkedHashMap<Symbol, Type>();
		complete = false;
	}

	public void set_tag(String tg)
	{
		tag = tg;
	}

	public void add_member(String name, Type tp)
	{
		field.put(Symbol.getSymbol(name), tp);
	}

	public Type get_member_type(String m)
	{
		Symbol csym = Symbol.getSymbol(m);
		return field.get(csym);
	}
	
	public String[] field_str()
	{
	    int lc = 2 + field.size();
	    String [] ret = new String[lc];
	    ret[0] = "{".intern();
	    ret[lc-1] = "}".intern();
	    
	    int cl = 1;
	    for(Symbol csym : field.keySet())
	    {
	        Type ct = field.get(csym);
	        ret[cl++] = String.format("  %s -> %s ", csym.name, ct.toString());
	    }
	    
	    return ret;
	}
}
