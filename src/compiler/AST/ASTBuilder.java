package compiler.AST;

import java.util.*;

import compiler.AST.FuncDec.Parameter;
import compiler.Parser.*;
import compiler.SymbolTable.*;
import compiler.Types.*;
import compiler.Types.Double;
import compiler.Types.Float;
import compiler.Types.Void;

public class ASTBuilder
{
	private Program entrance;
	private Prog ast;
	private int offset;
	private Env tenv, venv;

	public ASTBuilder(Program p)
	{
		entrance = p;
		ast = new Prog();
		offset = 0;

		tenv = ast.tenv;
		venv = ast.venv;
	}

	public Prog build()
	{
		for (ProgComp pc : entrance.elem)
		{
			if (pc instanceof Declaration)
			{
				Declaration x = (Declaration) pc;
				parseDeclaration(x, venv, ast);
			}
			else if (pc instanceof FuncDef)
			{
				FuncDef x = (FuncDef) pc;
				parseFuncDef(x, venv, ast);
			}
			else
			{
				panic("Internal Error.");
				return null;
			}
		}

		return ast;
	}

	private void parseDeclaration(Declaration x, Env y, Prog z)
	{
		Type def_type = parseTypeSpecifier(x.ts);
		for (InitDeclarator idr : x.elem)
		{
			// Name and Symbol
			String var_name = idr.declarator.plain_declarator.name;
			Symbol var_sym = Symbol.getSymbol(var_name);

			// Variable type
			Type var_type = resolve_type(def_type, idr.declarator);

			// Initializer
			Init initializer = parseInitializer(idr.initializer);
			boolean hasInitialized = initializer != null;

			// ASTNode
			VarDec var_dec = hasInitialized ? new VarDec(var_type, var_name, initializer) : new VarDec(var_type, var_name);
			z.add_dec(var_dec);

			// Environment
			VarEntry var_entry = new VarEntry(var_type, y, offset, hasInitialized);
			offset += var_type.width;
			y.put(var_sym, var_entry);
		}
	}

	private void parseFuncDef(FuncDef x, Env y, Prog z)
	{
		// Name and Symbol
		String func_name = x.pd.name;
		Symbol func_sym = Symbol.getSymbol(func_name);

		// Return type
		Type def_type = parseTypeSpecifier(x.ts);
		Type ret_type = resolve_plain_type(def_type, x.pd);

		// ASTNode
		FuncDec func_dec = new FuncDec(ret_type, func_name);
		func_dec.scope = new Env(y);
		for (PlainDeclaration pdn : x.pm)
		{
			String param_name = pdn.dlr.plain_declarator.name;
			Type param_type = parsePlainDeclaration(pdn);
			func_dec.add_param(param_type, param_name);
			func_dec.scope.put(Symbol.getSymbol(param_name), new VarEntry(param_type, func_dec.scope, offset, false));
			offset += param_type.width;
		}

		CompStmt cpst = parseCompoundStmt(x.cst, func_dec.scope);
		func_dec.set_body(cpst);
		z.add_dec(func_dec);

		// Environment
		Function func_type = new Function(new Void(), ret_type);
		ListIterator<Parameter> lit = func_dec.param.listIterator(func_dec.param.size());
		while (lit.hasPrevious())
		{
			Type ct = lit.previous().type;
			func_type = new Function(ct, func_type);
		}
		FuncEntry func_entry = new FuncEntry(func_type, y);
		y.put(func_sym, func_entry);
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

			if (tag != null)
			{
				ret.set_tag(tag);
				tenv.put(Symbol.getSymbol(tag), new TypeEntry(ret, tenv));
			}

			return ret;
		}
		else if (t.ts_type == TypeSpecifier.ts_union)
		{
			Union ret = new Union();
			String tag = t.name;

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

			if (tag != null)
			{
				ret.set_tag(tag);
				tenv.put(Symbol.getSymbol(tag), new TypeEntry(ret, tenv));
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
		if (x == null)
			return null;

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

	public CompStmt parseCompoundStmt(CompoundStatement x, Env y)
	{
		CompStmt ret = new CompStmt();
		ret.scope = y;

		for (Declaration decl : x.decls)
		{
			Type def_type = parseTypeSpecifier(decl.ts);
			for (InitDeclarator idr : decl.elem)
			{
				String vn = idr.declarator.plain_declarator.name;
				Type vt = resolve_type(def_type, idr.declarator);
				Init it = parseInitializer(idr.initializer);
			}
		}

		for (Statement st : x.stmts)
		{

		}
		return ret;
	}

	private Stmt parseStatement(Statement st)
	{
		if (st instanceof ExpressionStatement)
		{
			ExpressionStatement est = (ExpressionStatement) st;

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

		return null;
	}

	private Exp parseAssignExp(AssignmentExpr x)
	{
		return null;
	}

	private Object parseConstantExpr(ConstantExpr x)
	{
		BinaryExp ce = parseLogicalOrExpr(x.expr);
		if (ce.isConst)
			return ce.value;
		else
			return -1;
	}

	private BinaryExp parseLogicalOrExpr(LogicalOrExpr x)
	{
		return null;
	}

	private BinaryExp parseLogicalAndExpr(LogicalAndExpr x)
	{
		return null;
	}

	private void panic(String msg)
	{
		System.out.println(msg);
	}
}
