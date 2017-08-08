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

	public Init parseInitializer(Initializer x, Env y) throws Exception
	{
		if (x == null)
			return null;

		if (x.type == Initializer.assign)
		{
			Exp e = parseAssignExp(x.ae, y);
			return new Init(e);

		}
		else if (x.type == Initializer.list)
		{
			Init ret = new Init();
			for (Initializer it : x.comp)
			{
				Init cit = parseInitializer(it, y);
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
				Init it = parseInitializer(idr.initializer, y);
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

	private AssignExp parseAssignExp(AssignmentExpr x, Env y) throws Exception
	{
		return null;
	}

	private Object parseConstantExpr(ConstantExpr x, Env y) throws Exception
	{
		BinaryExp ce = parseLogicalOrExpr(x.expr, y);
		if (ce.isConst)
			return ce.value;
		else
			return null;
	}

	private BinaryExp parseLogicalOrExpr(LogicalOrExpr x, Env y) throws Exception
	{
		return null;
	}

	private BinaryExp parseLogicalAndExpr(LogicalAndExpr x, Env y) throws Exception
	{
		return null;
	}

	private BinaryExp parseInclusiveOrExpr(InclusiveOrExpr x, Env y) throws Exception
	{
		return null;
	}

	private BinaryExp parseExclusiveOrExpr(ExclusiveOrExpr x, Env y) throws Exception
	{
		return null;
	}

	private BinaryExp parseAndExpr(AndExpr x, Env y) throws Exception
	{
		return null;
	}

	private BinaryExp parseRelationalExpr(RelationalExpr x, Env y) throws Exception
	{
		return null;
	}

	private BinaryExp parseShiftExpr(ShiftExpr x, Env y) throws Exception
	{
		return null;
	}

	private BinaryExp parseAdditiveExpr(AdditiveExpr x, Env y) throws Exception
	{
		return null;
	}

	private BinaryExp parseMultiplicativeExpr(MultiplicativeExpr x, Env y) throws Exception
	{
		BinaryExp ret = new BinaryExp();
		Exp cur = ret;
		ListIterator<CastExpr> clit = x.expr_list.listIterator(x.expr_list.size());
		ListIterator<Integer> plit = x.op_list.listIterator(x.op_list.size());
		while (plit.hasPrevious())
		{
			
		}

		return ret;
	}

	private CastExp parseCastExpr(CastExpr x, Env y) throws Exception
	{
		UnaryExp ue = parseUnaryExpr(x.expr, y);
		CastExp ret = new CastExp(ue);

		Type cur_type = ue.type;
		ListIterator<TypeName> lit = x.type_list.listIterator(x.type_list.size());
		while (lit.hasPrevious())
		{
			Type ct = parseTypeName(lit.previous());
			if (cur_type.isConvertableTo(ct))
			{
				ret.add_type(ct);
				cur_type = ct;
			}
			else
				panic("Invalid type conversion.");
		}

		ret.decorate(cur_type, ue.isConst, ue.hasInitialized, false);
		if (ret.isConst)
		{
			// TODO
			ret.set_value(ue.value);
		}

		return ret;
	}

	private UnaryExp parseUnaryExpr(UnaryExpr x, Env y) throws Exception
	{
		UnaryExp ret = null;
		if (x.type == UnaryExpr.postfix)
		{
			PostfixExpr per = (PostfixExpr) x.elem;
			PostfixExp pe = parsePostfixExpr(per, y);
			ret = new UnaryExp(UnaryExpr.postfix, pe, null);
			ret.decorate(pe.type, pe.isConst, pe.hasInitialized, pe.isLvalue);
		}
		else if (x.type == UnaryExpr.inc)
		{
			UnaryExpr uer = (UnaryExpr) x.elem;
			UnaryExp ue = parseUnaryExpr(uer, y);
			if (!ue.isLvalue)
				panic("Not assignable.");
			boolean operable = Type.numeric(ue.type) || ue.type instanceof Pointer;
			if (!operable)
				panic("Can not be incremented.");

			ret = new UnaryExp(UnaryExpr.inc, ue, null);
			ret.decorate(ue.type, false, ue.hasInitialized, false);
		}
		else if (x.type == UnaryExpr.dec)
		{
			UnaryExpr uer = (UnaryExpr) x.elem;
			UnaryExp ue = parseUnaryExpr(uer, y);
			if (!ue.isLvalue)
				panic("Not assignable.");
			boolean operable = Type.numeric(ue.type) || ue.type instanceof Pointer;
			if (!operable)
				panic("Can not be decreased.");

			ret = new UnaryExp(UnaryExpr.dec, ue, null);
			ret.decorate(ue.type, false, ue.hasInitialized, false);
		}
		else if (x.type == UnaryExpr.address)
		{
			CastExpr cer = (CastExpr) x.elem;
			CastExp ce = parseCastExpr(cer, y);
			ret = new UnaryExp(UnaryExpr.address, ce, null);
			ret.decorate(new Pointer(ce.type), true, true, false);
		}
		else if (x.type == UnaryExpr.dereference)
		{
			CastExpr cer = (CastExpr) x.elem;
			CastExp ce = parseCastExpr(cer, y);
			Type cur_type = ce.type;
			if (cur_type instanceof Array)
			{
				Array ay = (Array) cur_type;
				ret = new UnaryExp(UnaryExpr.dereference, ce, null);
				ret.decorate(ay.elem_type, false, ce.hasInitialized, true);
			}
			else if (cur_type instanceof Pointer)
			{
				Pointer pr = (Pointer) cur_type;
				ret = new UnaryExp(UnaryExpr.dereference, ce, null);
				ret.decorate(pr.elem_type, false, ce.hasInitialized, true);
			}
			else
				panic("Only array or pointer can be dereferenced!");
		}
		else if (x.type == UnaryExpr.positive)
		{
			CastExpr cer = (CastExpr) x.elem;
			CastExp ce = parseCastExpr(cer, y);

			Type cur_type = ce.type;
			if (!Type.numeric(cur_type))
				panic("Not an numeric type.");

			ret = new UnaryExp(UnaryExpr.positive, ce, null);
			ret.decorate(cur_type, ce.isConst, ce.hasInitialized, false);
			if (ret.isConst)
				ret.set_value(ce.value);
		}
		else if (x.type == UnaryExpr.negative)
		{
			CastExpr cer = (CastExpr) x.elem;
			CastExp ce = parseCastExpr(cer, y);

			Type cur_type = ce.type;
			if (!Type.numeric(cur_type))
				panic("Not an numeric type.");

			ret = new UnaryExp(UnaryExpr.negative, ce, null);
			ret.decorate(cur_type, ce.isConst, ce.hasInitialized, false);
			if (ret.isConst)
			{
				if (cur_type instanceof Int)
				{
					int cval = ((Integer) ce.value).intValue();
					ret.set_value(new Integer(-cval));
				}
				else if (cur_type instanceof Char)
				{
					int cval = (int) ((Character) ce.value).charValue();
					ret.set_value(new Integer(-cval));
				}
				else if (cur_type instanceof Float)
				{
					float cval = (float) ce.value;
					ret.set_value(cval);
				}
				else if (cur_type instanceof Double)
				{
					double cval = (double) ce.value;
					ret.set_value(cval);
				}
				else
					panic("Internal Error.");
			}
		}
		else if (x.type == UnaryExpr.bit_not)
		{
			CastExpr cer = (CastExpr) x.elem;
			CastExp ce = parseCastExpr(cer, y);
			Type cur_type = ce.type;
			if (cur_type instanceof Int)
			{
				ret = new UnaryExp(UnaryExpr.bit_not, ce, null);
				ret.decorate(cur_type, ce.isConst, ce.hasInitialized, false);
				if (ret.isConst)
				{
					int cval = ((Integer) ce.value).intValue();
					ret.set_value(new Integer(~cval));
				}
			}
			else if (cur_type instanceof Char)
			{
				ret = new UnaryExp(UnaryExpr.bit_not, ce, null);
				ret.decorate(cur_type, ce.isConst, ce.hasInitialized, false);
				if (ret.isConst)
				{
					int cval = (int) ((Character) ce.value).charValue();
					ret.set_value(new Integer(~cval));
				}
			}
			else
				panic("Invalid operand.");
		}
		else if (x.type == UnaryExpr.not)
		{
			CastExpr cer = (CastExpr) x.elem;
			CastExp ce = parseCastExpr(cer, y);
			Type cur_type = ce.type;
			if (cur_type instanceof Pointer || Type.numeric(cur_type))
			{
				ret = new UnaryExp(UnaryExpr.not, ce, null);
				ret.decorate(new Int(), ce.isConst, ce.hasInitialized, false);
				if (ret.isConst)
				{
					if (cur_type instanceof Int)
					{
						int cval = ((Integer) ce.value).intValue();
						cval = cval == 0 ? 0 : 1;
						ret.set_value(new Integer(cval));
					}
					else if (cur_type instanceof Char)
					{
						int cval = (int) ((Character) ce.value).charValue();
						cval = cval == 0 ? 0 : 1;
						ret.set_value(new Integer(cval));
					}
					else if (cur_type instanceof Float)
					{
						float cval = (float) ce.value;
						if (cval == 0)
							ret.set_value(0);
						else
							ret.set_value(1);
					}
					else if (cur_type instanceof Double)
					{
						double cval = (double) ce.value;
						if (cval == 0)
							ret.set_value(0);
						else
							ret.set_value(1);
					}
					else if (cur_type instanceof Pointer)
					{
						int cval = ((Integer) ce.value).intValue();
						cval = cval == 0 ? 0 : 1;
						ret.set_value(new Integer(cval));
					}
					else
						panic("Internal Error.");
				}
			}
			else
				panic("Invalid operand.");
		}
		else if (x.type == UnaryExpr.sizeof)
		{
			if (x.elem instanceof UnaryExpr)
			{
				UnaryExpr uer = (UnaryExpr) x.elem;
				UnaryExp ue = parseUnaryExpr(uer, y);

				ret = new UnaryExp(UnaryExpr.sizeof, ue, null);
				ret.decorate(new Int(), true, true, false);
				ret.set_value(ue.type.width);
			}
			else if (x.elem instanceof TypeName)
			{
				TypeName tpn = (TypeName) x.elem;
				Type t = parseTypeName(tpn);

				ret = new UnaryExp(UnaryExpr.sizeof, null, t);
				ret.decorate(new Int(), true, true, false);
				ret.set_value(t.width);
			}
			else
				panic("Internal Error.");
		}
		else
			panic("Internal Error.");

		return ret;
	}

	private Type parseTypeName(TypeName x) throws Exception
	{
		Type ret = parseTypeSpecifier(x.type_specifier);
		for (int i = 0; i < x.star_cnt; i++)
			ret = new Pointer(ret);

		return ret;
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
					panic("Only function can be called.");
			}
			else if (pfx.type == PostfixExpr.dot)
			{
				if (cur_type instanceof Struct)
				{
					// Cast
					Struct pt = (Struct) cur_type;

					// Get member type
					String member = (String) pfx.content;
					cur_type = pt.get_member_type(member);

					// Check
					if (cur_type == null)
						panic("Not a member.");

					// Decorate the exp
					ret.add_elem(PostfixExpr.dot, null, member, cur_type);
					ret.decorate(cur_type, false, pe.hasInitialized, true);
				}
				else if (cur_type instanceof Union)
				{
					// Cast
					Union pt = (Union) cur_type;

					// Get member type
					String member = (String) pfx.content;
					cur_type = pt.get_member_type(member);

					// Check
					if (cur_type == null)
						panic("Not a member.");

					// Decorate the exp
					ret.add_elem(PostfixExpr.dot, null, member, cur_type);
					ret.decorate(cur_type, false, pe.hasInitialized, true);
				}
				else
					panic("Not a structure or union.");
			}
			else if (pfx.type == PostfixExpr.ptr)
			{
				if (cur_type instanceof Pointer)
				{
					Type pt = ((Pointer) cur_type).elem_type;
					if (pt instanceof Struct || pt instanceof Union)
					{
						// Cast
						Record cpt = (Record) pt;

						// Get member type
						String member = (String) pfx.content;
						cur_type = cpt.get_member_type(member);

						// Check
						if (cur_type == null)
							panic("Not a member.");

						// Decorate the exp
						ret.add_elem(PostfixExpr.ptr, null, member, cur_type);
						ret.decorate(cur_type, false, pe.hasInitialized, true);
					}
					else
						panic("Not a pointer to record.");
				}
				else
					panic("Not a pointer.");
			}
			else if (pfx.type == PostfixExpr.inc)
			{
				boolean operable = Type.numeric(cur_type) || cur_type instanceof Pointer;
				if (!operable)
					panic("Can not be incremented.");

				ret.add_elem(PostfixExpr.inc, null, null, cur_type);
			}
			else if (pfx.type == PostfixExpr.dec)
			{
				boolean operable = Type.numeric(cur_type) || cur_type instanceof Pointer;
				if (!operable)
					panic("Can not be decreased.");

				ret.add_elem(PostfixExpr.dec, null, null, cur_type);
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
