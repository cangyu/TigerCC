package compiler.Parser;

import java.util.*;
import compiler.SymbolTable.Env;

public class FuncDef extends ProgComp
{
	public TypeSpecifier ts;
	public PlainDeclarator pd;
	public LinkedList<PlainDeclaration> pm;
	public Env scope;
	public CompoundStatement cst;

	public FuncDef(TypeSpecifier _ts, PlainDeclarator _fn)
	{
		ts = _ts;
		pd = _fn;
		pm = new LinkedList<PlainDeclaration>();
		cst = null;
	}

	public void add_param(PlainDeclaration x)
	{
		pm.add(x);
	}

	public void add_body(CompoundStatement x)
	{
		cst = x;
	}
}
