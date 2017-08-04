package compiler.AST;

import java.util.*;
import compiler.Parser.*;
import compiler.SymbolTable.*;
import compiler.Types.*;
import compiler.Types.Double;
import compiler.Types.Float;
import compiler.Types.Void;

public class ASTBuilder
{
	private Program entrance;
	private Env tenv, venv;
	private LinkedList<Dec> decl;

	public ASTBuilder(Program p)
	{
		entrance = p;
		tenv = new Env(null);
		venv = new Env(null);
		decl = new LinkedList<Dec>();
	}

	public Prog get_ast()
	{
		return new Prog(decl, tenv, venv);
	}

	public void build()
	{
		for (ProgComp pc : entrance.elem)
			parseProgComp(pc);
	}

	public void parseProgComp(ProgComp pcp)
	{
		if (pcp instanceof Declaration)
		{
			Declaration x = (Declaration) pcp;
			Type dt = parseTypeSpecifier(x.ts);
			for (InitDeclarator idr : x.elem)
			{
				String vn = idr.declarator.plain_declarator.name;
				Type vt = resolve_type(dt, idr.declarator);
				Init it = parseInitializer(idr.initializer);
				decl.add(new VarDec(vt, vn, it));
			}
		}
		else if (pcp instanceof FuncDef)
		{
			FuncDef x = (FuncDef) pcp;
			Type dt = parseTypeSpecifier(x.ts);
			Type ft = resolve_plain_type(dt, x.pd);
			String fn = x.pd.name;
			FuncDec fd = new FuncDec(ft, fn);
			for (PlainDeclaration pdn : x.pm)
			{
				String vn = pdn.dlr.plain_declarator.name;
				Type vt = parsePlainDeclaration(pdn);
				fd.add_param(vt, vn);
			}
			CompStmt cpst = parseCompoundStmt(x.cst);
			fd.set_body(cpst);
			decl.add(fd);
		}
		else
			panic("Internal Error.");
	}

	public Type parseTypeSpecifier(TypeSpecifier t)
	{
		if (t.ts_type == TypeSpecifier.ts_void)
			return new Void();
		else if (t.ts_type == TypeSpecifier.ts_int)
			return new Int();
		else if (t.ts_type == TypeSpecifier.ts_char)
			return new Char();
		else if (t.ts_type == TypeSpecifier.ts_float)
			return new Float();
		else if (t.ts_type == TypeSpecifier.ts_double)
			return new Double();
		else if (t.ts_type == TypeSpecifier.ts_struct)
		{
			Struct ret = new Struct();
			String tag = t.name;
			if (tag != null)
				ret.set_tag(tag);

			for (RecordEntry re : t.entry)
			{
				Type dt = parseTypeSpecifier(re.ts);
				for (Declarator dclr : re.dls)
				{
					String name = dclr.plain_declarator.name;
					Type ct = resolve_type(dt, dclr);
					ret.add_record(ct, name);
				}
			}
			return ret;
		}
		else if (t.ts_type == TypeSpecifier.ts_union)
		{
			Union ret = new Union();
			String tag = t.name;
			if (tag != null)
				ret.set_tag(tag);

			for (RecordEntry re : t.entry)
			{
				Type dt = parseTypeSpecifier(re.ts);
				for (Declarator dclr : re.dls)
				{
					String name = dclr.plain_declarator.name;
					Type ct = resolve_type(dt, dclr);
					ret.add_record(ct, name);
				}
			}
			return ret;
		}
		else
		{
			panic("Internal Error!");
			return null;
		}
	}

	public Type resolve_type(Type base, Declarator dr)
	{
		Type ret = resolve_plain_type(base, dr.plain_declarator);
		for (ConstantExpr ce : dr.dimension)
		{
			int cnt = parseConstantExpr(ce);
			if (cnt < 0)
				panic("Invalid array definition.");
			else
				ret = new Array(cnt, ret);
		}

		return ret;
	}

	public Type resolve_plain_type(Type base, PlainDeclarator x)
	{
		Type ret = base;
		for (int i = 0; i < x.star_num; i++)
			ret = new Pointer(ret);
		return ret;
	}

	public Init parseInitializer(Initializer x)
	{
		if (x.type == Initializer.assign)
		{
			Exp e = parseAssignExp(x.ae);
			return new Init(e);

		}
		else if (x.type == Initializer.list)
		{
			Init ret = new Init();
			for (Initializer it : x.comp)
			{
				Init cit = parseInitializer(it);
				ret.add_init(cit);
			}
			return ret;
		}
		else
		{
			panic("Internal Error.");
			return null;
		}
	}

	public Type parsePlainDeclaration(PlainDeclaration x)
	{
		Type dt = parseTypeSpecifier(x.ts);
		return resolve_type(dt, x.dlr);
	}

	public CompStmt parseCompoundStmt(CompoundStatement x)
	{
		CompStmt ret = new CompStmt();
		ret.scope = new Env(venv);

		for (Declaration decl : x.decls)
		{
			Type dt = parseTypeSpecifier(decl.ts);
			for (InitDeclarator idr : decl.elem)
			{
				String vn = idr.declarator.plain_declarator.name;
				Type vt = resolve_type(dt, idr.declarator);
				Init it = parseInitializer(idr.initializer);
			}
		}

		for (Statement st : x.stmts)
		{
			if (st instanceof ExpressionStatement)
			{

			}
			else if (st instanceof CompoundStatement)
			{

			}
			else if (st instanceof SelectionStatement)
			{

			}
			else if (st instanceof IterationStatement)
			{

			}
			else if (st instanceof JumpStatement)
			{

			}
			else
			{
				panic("Internal Error.");
				return null;
			}
		}
		return ret;
	}

	public int parseConstantExpr(ConstantExpr x)
	{
		return -1;
	}

	public Exp parseAssignExp(AssignmentExpr x)
	{
		return null;
	}

	private void panic(String msg)
	{
		System.out.println(msg);
	}
}
