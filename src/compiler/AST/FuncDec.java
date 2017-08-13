package compiler.AST;

import compiler.SymbolTable.Env;
import compiler.Typing.*;
import java.util.*;

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
	public CompStmt body;

	public FuncDec(Type t, String fn)
	{
		ret_type = t;
		name = fn;
		param = new ArrayList<Parameter>();
	}

	public void add_param(String vn, Type t)
	{
		param.add(new Parameter(vn, t));
	}

	public void set_body(CompStmt cs)
	{
		body = cs;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
