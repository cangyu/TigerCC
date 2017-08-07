package compiler.AST;

import compiler.SymbolTable.Env;
import compiler.Types.*;
import java.util.*;

public class FuncDec extends Dec
{
	class Parameter
	{
		public Type type;
		public String name;

		public Parameter(Type t, String text)
		{
			type = t;
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

	public void add_param(Type t, String vn)
	{
		param.add(new Parameter(t, vn));
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
