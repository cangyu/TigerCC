package compiler.AST;

import java.util.*;

import compiler.AST.FuncDec.Parameter;
import compiler.Parser.*;
import compiler.Parser.PostfixExpr.Postfix;
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

	public Prog build() throws Exception
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
				panic("Internal Error.");
		}

		return ast;
	}

	private void parseDeclaration(Declaration x, Env y, Prog z) throws Exception
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
			VarEntry var_entry = new VarEntry(var_type, offset, hasInitialized);
			offset += var_type.width;
			y.put(var_sym, var_entry);
		}
	}

	private void parseFuncDef(FuncDef x, Env y, Prog z) throws Exception
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
			func_dec.scope.put(Symbol.getSymbol(param_name), new VarEntry(param_type, offset, false));
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
		FuncEntry func_entry = new FuncEntry(func_type);
		y.put(func_sym, func_entry);
	}

	public Type parseTypeSpecifier(TypeSpecifier t) throws Exception
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
				tenv.put(Symbol.getSymbol(tag), new TypeEntry(ret));
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
				tenv.put(Symbol.getSymbol(tag), new TypeEntry(ret));
			}

			return ret;
		}
		else
		{
			panic("Internal Error!");
			return null;
		}
	}

	public Type resolve_type(Type base, Declarator dr) throws Exception
	{
		Type ret = resolve_plain_type(base, dr.plain_declarator);
		for (ConstantExpr ce : dr.dimension)
		{
			Object val = parseConstantExpr(ce);
			if (val instanceof Integer)
			{
				int cnt = ((Integer) val).intValue();
				ret = new Array(cnt, ret);
			}
			else
				panic("Invalid array definition.");
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

	public Init parseInitializer(Initializer x) throws Exception
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

	public Type parsePlainDeclaration(PlainDeclaration x) throws Exception
	{
		Type dt = parseTypeSpecifier(x.ts);
		return resolve_type(dt, x.dlr);
	}

	public CompStmt parseCompoundStmt(CompoundStatement x, Env y) throws Exception
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

	private Stmt parseStatement(Statement st) throws Exception
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

	private CommaExp parseExpression(Expression x, Env y) throws Exception
	{
		return null;
	}

	private AssignExp parseAssignExp(AssignmentExpr x)
	{
		return null;
	}

	private Object parseConstantExpr(ConstantExpr x)
	{
		BinaryExp ce = parseLogicalOrExpr(x.expr);
		if (ce.isConst)
			return ce.value;
		else
			return null;
	}

	private BinaryExp parseLogicalOrExpr(LogicalOrExpr x)
	{
		return null;
	}

	private BinaryExp parseLogicalAndExpr(LogicalAndExpr x)
	{
		return null;
	}

	private CastExp parseCastExpr(CastExpr x, Env y) throws Exception
	{
		return null;
	}

	private UnaryExp parseUnaryExpr(UnaryExpr x, Env y) throws Exception
	{
		return null;
	}

	private PostfixExp parsePostfixExpr(PostfixExpr x, Env y) throws Exception
	{
		PrimaryExp pe = parsePrimaryExpr(x.expr, y);

		PostfixExp ret = new PostfixExp(pe);
		Type cur_type = pe.type;

		ListIterator<Postfix> lit = x.elem.listIterator();
		while (lit.hasNext())
		{
			Postfix pfx = lit.next();
			if (pfx.type == PostfixExpr.mparen)
			{
				CommaExp ce = parseExpression((Expression) pfx.content, y);
				if (ce.type instanceof Char || ce.type instanceof Int)
				{
					boolean init_flag = pe.hasInitialized && ce.hasInitialized;
					if (cur_type instanceof Pointer)
					{
						cur_type = ((Pointer) cur_type).elem_type;
						ret.add_elem(PostfixExpr.mparen, ce, null, cur_type);
						ret.decorate(cur_type, false, init_flag, true);
					}
					else if (cur_type instanceof Array)
					{
						cur_type = ((Array) cur_type).elem_type;
						ret.add_elem(PostfixExpr.mparen, ce, null, cur_type);
						ret.decorate(cur_type, false, init_flag, true);
					}
					else
						panic("Only pointer or array can be indexed!");
				}
				else
					panic("Index is not an integer.");
			}
			else if (pfx.type == PostfixExpr.paren)
			{
				if (cur_type instanceof Function)
				{
					Expression arg = (Expression) pfx.content;
					CommaExp ce = parseExpression(arg, y);

					// function definition arguments type
					LinkedList<Type> fdatp = Function.get_param_type((Function) cur_type);

					if (ce.exp.size() != fdatp.size() - 1)
						panic("Function arguments quantity doesn't match.");

					// parameter list iterator
					ListIterator<AssignExp> plit = ce.exp.listIterator();

					// argument type list iterator
					ListIterator<Type> atlit = fdatp.listIterator();

					while (plit.hasNext())
					{
						Type cptp = plit.next().type;
						Type catp = atlit.next();
						if (!cptp.isConvertableTo(catp))
							panic("Incompatible parameter type.");
					}
					cur_type = fdatp.getLast();
					ret.add_elem(PostfixExpr.paren, ce, null, cur_type);
					ret.decorate(cur_type, false, true, false);
				}
				else
					panic("Only function can be called!");
			}
			else if (pfx.type == PostfixExpr.dot)
			{

			}
			else if (pfx.type == PostfixExpr.ptr)
			{

			}
			else if (pfx.type == PostfixExpr.inc)
			{

			}
			else if (pfx.type == PostfixExpr.dec)
			{

			}
			else
				panic("Internal Error.");
		}

		return ret;
	}

	private PrimaryExp parsePrimaryExpr(PrimaryExpr x, Env y) throws Exception
	{
		PrimaryExp ret = new PrimaryExp();
		if (x.type == PrimaryExpr.identifier)
		{
			String var_name = (String) x.elem;
			Symbol var_sym = Symbol.getSymbol(var_name);
			Entry entry = y.get(var_sym);

			if (entry == null)
				panic("Symbol \'" + var_name + "\' is undefined.");

			if (entry instanceof VarEntry)
			{
				VarEntry var_entry = (VarEntry) entry;
				ret.decorate(var_entry.type, false, var_entry.hasInitialized, true);
			}
			else if (entry instanceof FuncEntry)
			{
				FuncEntry func_entry = (FuncEntry) entry;
				ret.decorate(func_entry.type, false, true, false);
			}
			else
				panic("Can not use a type as identifier!");
		}
		else if (x.type == PrimaryExpr.integer_constant)
		{
			ret.decorate(new Int(), true, true, false);
			ret.set_value(x.elem);
		}
		else if (x.type == PrimaryExpr.character_constant)
		{
			ret.decorate(new Char(), true, true, false);
			ret.set_value(x.elem);
		}
		else if (x.type == PrimaryExpr.real_constant)
		{
			ret.decorate(new Float(), true, true, false);
			ret.set_value(x.elem);
		}
		else if (x.type == PrimaryExpr.string)
		{
			ret.decorate(new Pointer(new Char()), false, true, false); // GCC doesn't consider "abcd"[2] as a const
			ret.set_value(x.elem);
		}
		else if (x.type == PrimaryExpr.paren_expr)
		{
			CommaExp ce = parseExpression((Expression) x.elem, y);
			ret.decorate(ce.type, ce.isConst, ce.hasInitialized, false);// GCC doesn't consider (a, b, c) as a lvalue
			ret.set_value(ce.value);
			ret.set_expr(ce);
		}
		else
			panic("Internal Error.");

		return ret;
	}

	private void panic(String msg) throws Exception
	{
		throw new Exception(msg);
	}
}
