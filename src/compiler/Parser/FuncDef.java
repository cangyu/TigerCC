package compiler.Parser;

import compiler.AST.ASTNodeVisitor;
import java.util.*;

public class FuncDef extends ProgramComp
{
	public TypeSpecifier ts;
	public PlainDeclarator pd;
	public LinkedList<PlainDeclaration> pm;
	public CompoundStmt cst;

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

	public void add_body(CompoundStmt x)
	{
		cst = x;
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}