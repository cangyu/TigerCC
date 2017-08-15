package compiler.AST;

import java.util.*;
import compiler.Typing.*;
import compiler.Scoping.Env;

public class FuncDec extends Dec
{
	class Parameter
	{
		public String name;
		public Type type;

		public Parameter(String text, Type tp)
		{
			type = tp;
			name = text;
		}
	}

	public Type ret_type;
	public String name;
	public ArrayList<Parameter> param;
	public Env scope;
	public LinkedList<VarDec> var;
	public LinkedList<Stmt> st;

	public FuncDec(Type t, String fn, int off)
	{
		super(off);
		ret_type = t;
		name = fn;
		param = new ArrayList<Parameter>();
		var = new LinkedList<VarDec>();
		st = new LinkedList<Stmt>();
	}

	public void add_param(String vn, Type t)
	{
		param.add(new Parameter(vn, t));
	}

	public void add_var(VarDec vd)
	{
		var.add(vd);
	}

	public void add_stmt(Stmt s)
	{
		st.add(s);
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
