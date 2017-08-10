package compiler.AST;

import java.util.*;
import compiler.AST.FuncDec.Parameter;
import compiler.Parser.*;
import compiler.Parser.PostfixExpr.Postfix;
import compiler.SymbolTable.*;
import compiler.Typing.*;
import compiler.Typing.Void;

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
		Type def_type = parseTypeSpecifier(x.ts, y);
		for (InitDeclarator idr : x.elem)
		{
			// Name and Symbol
			String var_name = idr.declarator.plain_declarator.name;
			Symbol var_sym = Symbol.getSymbol(var_name);

			// Variable type
			Type var_type = resolve_type(def_type, idr.declarator, y);

			// Initializer
			Init initializer = parseInitializer(idr.initializer, y);

			// ASTNode
			VarDec var_dec = new VarDec(var_type, var_name, initializer);
			z.add_dec(var_dec);

			// Environment
			boolean hasInitialized = initializer != null;
			VarEntry var_entry = new VarEntry(var_type, offset, hasInitialized, true, false);
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
		Type def_type = parseTypeSpecifier(x.ts, y);
		Type ret_type = resolve_plain_type(def_type, x.pd);

		// ASTNode
		FuncDec func_dec = new FuncDec(ret_type, func_name);
		func_dec.scope = new Env(y);
		for (PlainDeclaration pdn : x.pm)
		{
			String param_name = pdn.dlr.plain_declarator.name;
			Symbol param_sym = Symbol.getSymbol(param_name);
			Type param_type = parsePlainDeclaration(pdn, y);
			VarEntry param_entry = new VarEntry(param_type, offset, false, true, false);
			func_dec.add_param(param_type, param_name);
			func_dec.scope.put(param_sym, param_entry);
			offset += param_type.width;
		}

		CompStmt cpst = parseCompoundStmt(x.cst, func_dec.scope);
		func_dec.set_body(cpst);
		z.add_dec(func_dec);

		// Environment
		Function func_type = new Function(Void.instance, ret_type);
		ListIterator<Parameter> lit = func_dec.param.listIterator(func_dec.param.size());
		while (lit.hasPrevious())
		{
			Type ct = lit.previous().type;
			func_type = new Function(ct, func_type);
		}
		FuncEntry func_entry = new FuncEntry(func_type);
		y.put(func_sym, func_entry);
	}

	private Type parseTypeSpecifier(TypeSpecifier t, Env y) throws Exception
	{
		if (t.ts_type == TypeSpecifier.ts_void)
			return Void.instance;
		else if (t.ts_type == TypeSpecifier.ts_int)
			return Int.instance;
		else if (t.ts_type == TypeSpecifier.ts_char)
			return Char.instance;
		else if (t.ts_type == TypeSpecifier.ts_float || t.ts_type == TypeSpecifier.ts_double)
			return FP.instance;
		else if (t.ts_type == TypeSpecifier.ts_struct)
		{
			Struct ret = new Struct();
			String tag = t.name;

			for (RecordEntry re : t.entry)
			{
				Type dt = parseTypeSpecifier(re.ts, y);
				for (Declarator dclr : re.dls)
				{
					String name = dclr.plain_declarator.name;
					Type ct = resolve_type(dt, dclr, y);
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
				Type dt = parseTypeSpecifier(re.ts, y);
				for (Declarator dclr : re.dls)
				{
					String name = dclr.plain_declarator.name;
					Type ct = resolve_type(dt, dclr, y);
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

	private Type resolve_type(Type base, Declarator dr, Env y) throws Exception
	{
		Type ret = resolve_plain_type(base, dr.plain_declarator);
		for (ConstantExpr ce : dr.dimension)
		{
			Object val = parseConstantExpr(ce, y);
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

	private Type resolve_plain_type(Type base, PlainDeclarator x)
	{
		Type ret = base;
		for (int i = 0; i < x.star_num; i++)
			ret = new Pointer(ret);
		return ret;
	}

	private Init parseInitializer(Initializer x, Env y) throws Exception
	{
		if (x == null)
			return null;

		if (x.type == Initializer.assign)
		{
			AssignExp e = parseAssignmentExpr(x.ae, y);
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

	private Type parsePlainDeclaration(PlainDeclaration x, Env y) throws Exception
	{
		Type dt = parseTypeSpecifier(x.ts, y);
		return resolve_type(dt, x.dlr, y);
	}

	private Stmt parseStatement(Statement st, Env y) throws Exception
	{
		Stmt ret = null;
		if (st instanceof ExpressionStatement)
			ret = parseExpressionStatement((ExpressionStatement) st, y);
		else if (st instanceof CompoundStatement)
			ret = parseCompoundStmt((CompoundStatement) st, y);
		else if (st instanceof SelectionStatement)
			ret = parseSelectionStatement((SelectionStatement) st, y);
		else if (st instanceof IterationStatement)
			ret = parseIterationStatement((IterationStatement) st, y);
		else if (st instanceof JumpStatement)
			ret = parseJumpStatement((JumpStatement) st, y);
		else
			panic("Internal Error.");
		return ret;
	}

	private ExprStmt parseExpressionStatement(ExpressionStatement x, Env y) throws Exception
	{
		ExprStmt ret = null;
		if (x.elem == null)
			ret = new ExprStmt(null);
		else
		{
			CommaExp e = parseExpression(x.elem, y);
			ret = new ExprStmt(e);
		}
		return ret;
	}

	private CompStmt parseCompoundStmt(CompoundStatement x, Env y) throws Exception
	{
		CompStmt ret = new CompStmt();
		ret.scope = y;

		for (Declaration decl : x.decls)
		{
			Type def_type = parseTypeSpecifier(decl.ts, y);
			for (InitDeclarator idr : decl.elem)
			{
				String var_name = idr.declarator.plain_declarator.name;
				Symbol var_sym = Symbol.getSymbol(var_name);
				Type var_type = resolve_type(def_type, idr.declarator, y);
				Init var_it = parseInitializer(idr.initializer, y);
				boolean hasInit = var_it != null;
				VarEntry var_entry = new VarEntry(var_type, offset, hasInit, true, false);
				VarDec var_dec = new VarDec(var_type, var_name, var_it);
				ret.scope.put(var_sym, var_entry);
				ret.add_var(var_dec);
			}
		}

		for (Statement st : x.stmts)
		{
			Stmt s = parseStatement(st, y);
			ret.add_stmt(s);
		}

		return ret;
	}

	private SelectStmt parseSelectionStatement(SelectionStatement x, Env y) throws Exception
	{
		CommaExp ce = parseExpression(x.cond, y);

		Stmt stt = parseStatement(x.if_clause, y);
		Stmt stf = null;
		if (x.else_clause != null)
			stf = parseStatement(x.else_clause, y);

		return new SelectStmt(ce, stt, stf);
	}

	private IterStmt parseIterationStatement(IterationStatement x, Env y) throws Exception
	{
		IterStmt ret = null;

		if (x.type == IterationStatement.WHILE)
		{
			CommaExp ce = parseExpression(x.judge, y);
			Stmt st = parseStatement(x.stmt, y);
			ret = new IterStmt(ce, st);
		}
		else if (x.type == IterationStatement.FOR)
		{
			CommaExp ce1 = parseExpression(x.init, y);
			CommaExp ce2 = parseExpression(x.judge, y);
			CommaExp ce3 = parseExpression(x.next, y);
			Stmt st = parseStatement(x.stmt, y);
			ret = new IterStmt(ce1, ce2, ce3, st);
		}
		else
			panic("Internal Error.");

		return ret;
	}

	private JumpStmt parseJumpStatement(JumpStatement x, Env y) throws Exception
	{
		if (x.type == JumpStatement.CTNU)
			return new JumpStmt(JumpStmt.jp_ctn);
		else if (x.type == JumpStatement.BRK)
			return new JumpStmt(JumpStmt.jp_brk);
		else
		{
			if (x.expr == null)
				return new JumpStmt(JumpStmt.jp_ret);
			else
			{
				CommaExp ce = parseExpression(x.expr, y);
				return new JumpStmt(ce);
			}
		}
	}

	private CommaExp parseExpression(Expression x, Env y) throws Exception
	{
		if (x == null)
			return null;

		// build
		CommaExp ret = new CommaExp();
		ListIterator<AssignmentExpr> lit = x.elem.listIterator();
		while (lit.hasNext())
		{
			AssignExp ae = parseAssignmentExpr(lit.next(), y);
			ret.add_exp(ae);
		}

		// decorate
		AssignExp last = ret.exp.getLast();
		ret.decorate(last.type, last.isConst, last.hasInitialized, false);
		if (ret.isConst)
			ret.set_value(last.value);

		return ret;
	}

	private AssignExp parseAssignmentExpr(AssignmentExpr x, Env y) throws Exception
	{
		AssignExp ret = new AssignExp();

		// Check right-hand-side
		if (x.rexpr == null)
			panic("Internal Error.");

		// Build right expression first
		ret.right = parseLogicalOrExpr(x.rexpr, y);

		// Check left-hand-side
		if (x.op_list.size() != x.lexpr_list.size())
			panic("Internal Error.");

		ListIterator<Integer> alit = x.op_list.listIterator(x.op_list.size());
		ListIterator<UnaryExpr> ulit = x.lexpr_list.listIterator(x.lexpr_list.size());

		// Build left side iteratively
		while (alit.hasPrevious())
		{
			ret.assign_type = alit.previous().intValue();
			ret.left = parseUnaryExpr(ulit.previous(), y);

			if (alit.hasPrevious())
			{
				AssignExp nrt = new AssignExp();
				nrt.right = ret;
				ret = nrt;
			}
		}

		// Decorate
		// C11: An assignment expression has the value of the left operand after the assignment.
		if (ret.left != null)
			ret.decorate(ret.left.type, false, true, false);
		else
		{
			ret.decorate(ret.right.type, ret.right.isConst, ret.right.hasInitialized, false);
			if (ret.isConst)
				ret.set_value(ret.right.value);
		}

		return ret;
	}

	private Object parseConstantExpr(ConstantExpr x, Env y) throws Exception
	{
		BinaryExp ce = parseLogicalOrExpr(x.expr, y);

		// In this simplified grammar, constant-expression is used only in array index
		// So, it should be an integer-constant, no need to decorate here
		if (ce.isConst)
			return ce.value;
		else
			return null;
	}

	private BinaryExp parseLogicalOrExpr(LogicalOrExpr x, Env y) throws Exception
	{
		// defensive check
		if (x.expr_list.isEmpty())
			panic("Internal Error.");

		ListIterator<LogicalAndExpr> clit = x.expr_list.listIterator();

		// first expression
		BinaryExp ret = new BinaryExp();
		ret.left = parseLogicalAndExpr(clit.next(), y);
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (clit.hasNext())
		{
			ret.op = BinaryExp.OR;
			ret.right = parseLogicalAndExpr(clit.next(), y);
			if (ret.left.isConst && ret.right.isConst)
			{
				ret.decorate(Int.instance, true, true, false);
				ret.calc_const_val();
			}

			if (clit.hasNext())
			{
				BinaryExp nrt = new BinaryExp();
				nrt.left = ret;
				ret = nrt;
			}
		}

		return ret;
	}

	private BinaryExp parseLogicalAndExpr(LogicalAndExpr x, Env y) throws Exception
	{
		// defensive check
		if (x.expr_list.isEmpty())
			panic("Internal Error.");

		ListIterator<InclusiveOrExpr> clit = x.expr_list.listIterator();

		// first expression
		BinaryExp ret = new BinaryExp();
		ret.left = parseInclusiveOrExpr(clit.next(), y);
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (clit.hasNext())
		{
			ret.op = BinaryExp.AND;
			ret.right = parseInclusiveOrExpr(clit.next(), y);
			if (ret.left.isConst && ret.right.isConst)
			{
				ret.decorate(Int.instance, true, true, false);
				ret.calc_const_val();
			}

			if (clit.hasNext())
			{
				BinaryExp nrt = new BinaryExp();
				nrt.left = ret;
				ret = nrt;
			}
		}

		return ret;
	}

	private BinaryExp parseInclusiveOrExpr(InclusiveOrExpr x, Env y) throws Exception
	{
		// defensive check
		if (x.expr_list.isEmpty())
			panic("Internal Error.");

		ListIterator<ExclusiveOrExpr> clit = x.expr_list.listIterator();

		// first expression
		BinaryExp ret = new BinaryExp();
		ret.left = parseExclusiveOrExpr(clit.next(), y);
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (clit.hasNext())
		{
			ret.op = BinaryExp.BIT_OR;
			ret.right = parseExclusiveOrExpr(clit.next(), y);
			if (ret.left.isConst && ret.right.isConst)
			{
				ret.decorate(Int.instance, true, true, false);
				ret.calc_const_val();
			}

			if (clit.hasNext())
			{
				BinaryExp nrt = new BinaryExp();
				nrt.left = ret;
				ret = nrt;
			}
		}

		return ret;
	}

	private BinaryExp parseExclusiveOrExpr(ExclusiveOrExpr x, Env y) throws Exception
	{
		// defensive check
		if (x.expr_list.isEmpty())
			panic("Internal Error.");

		ListIterator<AndExpr> clit = x.expr_list.listIterator();

		// first expression
		BinaryExp ret = new BinaryExp();
		ret.left = parseAndExpr(clit.next(), y);
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (clit.hasNext())
		{
			ret.op = BinaryExp.BIT_XOR;
			ret.right = parseAndExpr(clit.next(), y);
			if (clit.hasNext())
			{
				BinaryExp nrt = new BinaryExp();
				nrt.left = ret;
				ret = nrt;
			}
		}

		return ret;
	}

	private BinaryExp parseAndExpr(AndExpr x, Env y) throws Exception
	{
		// defensive check
		if (x.expr_list.isEmpty())
			panic("Internal Error.");

		ListIterator<EqualityExpr> clit = x.expr_list.listIterator();

		// first expression
		BinaryExp ret = new BinaryExp();
		ret.left = parseEqualityExpr(clit.next(), y);
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (clit.hasNext())
		{
			ret.op = BinaryExp.BIT_AND;
			ret.right = parseEqualityExpr(clit.next(), y);
			if (ret.left.isConst && ret.right.isConst)
			{
				ret.decorate(Int.instance, true, true, false);
				ret.calc_const_val();
			}

			if (clit.hasNext())
			{
				BinaryExp nrt = new BinaryExp();
				nrt.left = ret;
				ret = nrt;
			}
		}

		return ret;
	}

	private BinaryExp parseEqualityExpr(EqualityExpr x, Env y) throws Exception
	{
		// defensive check
		if (x.expr_list.size() != x.op_list.size() + 1)
			panic("Internal Error.");

		ListIterator<RelationalExpr> clit = x.expr_list.listIterator();
		ListIterator<Integer> plit = x.op_list.listIterator();

		// first expression
		BinaryExp ret = new BinaryExp();
		ret.left = parseRelationalExpr(clit.next(), y);
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (plit.hasNext())
		{
			ret.op = plit.next().intValue();
			ret.right = parseRelationalExpr(clit.next(), y);
			if (ret.left.isConst && ret.right.isConst)
			{
				ret.decorate(Int.instance, true, true, false);
				ret.calc_const_val();
			}

			if (plit.hasNext())
			{
				BinaryExp nrt = new BinaryExp();
				nrt.left = ret;
				ret = nrt;
			}
		}

		return ret;
	}

	private BinaryExp parseRelationalExpr(RelationalExpr x, Env y) throws Exception
	{
		// defensive check
		if (x.expr_list.size() != x.op_list.size() + 1)
			panic("Internal Error.");

		ListIterator<ShiftExpr> clit = x.expr_list.listIterator();
		ListIterator<Integer> plit = x.op_list.listIterator();

		// first expression
		BinaryExp ret = new BinaryExp();
		ret.left = parseShiftExpr(clit.next(), y);
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (plit.hasNext())
		{
			ret.op = plit.next().intValue();
			ret.right = parseShiftExpr(clit.next(), y);
			if (ret.left.isConst && ret.right.isConst)
			{
				ret.decorate(Int.instance, true, true, false);
				ret.calc_const_val();
			}

			if (plit.hasNext())
			{
				BinaryExp nrt = new BinaryExp();
				nrt.left = ret;
				ret = nrt;
			}
		}

		return ret;
	}

	private BinaryExp parseShiftExpr(ShiftExpr x, Env y) throws Exception
	{
		// defensive check
		if (x.expr_list.size() != x.op_list.size() + 1)
			panic("Internal Error.");

		ListIterator<AdditiveExpr> clit = x.expr_list.listIterator();
		ListIterator<Integer> plit = x.op_list.listIterator();

		// first expression
		BinaryExp ret = new BinaryExp();
		ret.left = parseAdditiveExpr(clit.next(), y);
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (plit.hasNext())
		{
			ret.op = plit.next().intValue();
			ret.right = parseAdditiveExpr(clit.next(), y);
			if (ret.left.isConst && ret.right.isConst)
			{
				ret.decorate(Int.instance, true, true, false);
				ret.calc_const_val();
			}

			if (plit.hasNext())
			{
				BinaryExp nrt = new BinaryExp();
				nrt.left = ret;
				ret = nrt;
			}
		}

		return ret;
	}

	private BinaryExp parseAdditiveExpr(AdditiveExpr x, Env y) throws Exception
	{
		// defensive check
		if (x.expr_list.size() != x.op_list.size() + 1)
			panic("Internal Error.");

		ListIterator<MultiplicativeExpr> clit = x.expr_list.listIterator();
		ListIterator<Integer> plit = x.op_list.listIterator();

		// first expression
		BinaryExp ret = new BinaryExp();
		ret.left = parseMultiplicativeExpr(clit.next(), y);
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (plit.hasNext())
		{
			ret.op = plit.next().intValue();
			ret.right = parseMultiplicativeExpr(clit.next(), y);
			if (ret.left.isConst && ret.right.isConst)
			{
				ret.decorate(Int.instance, true, true, false);
				ret.calc_const_val();
			}

			if (plit.hasNext())
			{
				BinaryExp nrt = new BinaryExp();
				nrt.left = ret;
				ret = nrt;
			}
		}

		return ret;
	}

	private BinaryExp parseMultiplicativeExpr(MultiplicativeExpr x, Env y) throws Exception
	{
		// defensive check
		if (x.expr_list.size() != x.op_list.size() + 1)
			panic("Internal Error.");

		ListIterator<CastExpr> clit = x.expr_list.listIterator();
		ListIterator<Integer> plit = x.op_list.listIterator();

		// first cast-expression
		BinaryExp ret = new BinaryExp();
		ret.left = parseCastExpr(clit.next(), y);
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (plit.hasNext())
		{
			int cop = plit.next().intValue();
			ret.op = cop;
			CastExpr rcer = clit.next();
			CastExp rce = parseCastExpr(rcer, y);
			ret.right = rce;

			if (ret.left.isConst && ret.right.isConst)
			{
				ret.decorate(Int.instance, true, true, false);
				ret.calc_const_val();
			}

			if (plit.hasNext())
			{
				BinaryExp nrt = new BinaryExp();
				nrt.left = ret;
				ret = nrt;
			}
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
			Type ct = parseTypeName(lit.previous(), y);
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
			ret.set_value(ue.value);

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
				else if (cur_type instanceof FP)
				{
					double cval = ((Double) ce.value).doubleValue();
					ret.set_value(new Double(-cval));
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
					else if (cur_type instanceof FP)
					{
						double cval = ((Double) ce.value).doubleValue();
						if (cval == 0)
							ret.set_value(new Integer(0));
						else
							ret.set_value(new Integer(1));
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
				Type t = parseTypeName(tpn, y);

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

	private Type parseTypeName(TypeName x, Env y) throws Exception
	{
		Type ret = parseTypeSpecifier(x.type_specifier, y);
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
				ret.decorate(var_entry.type, var_entry.isConst, var_entry.hasInitialized, var_entry.isLval);
			}
			else if (entry instanceof FuncEntry)
			{
				FuncEntry func_entry = (FuncEntry) entry;
				ret.decorate(func_entry.type, true, true, false);
			}
			else
				panic("Can not use a type as identifier!");
		}
		else if (x.type == PrimaryExpr.integer_constant)
		{
			ret.decorate(Int.instance, true, true, false);
			ret.set_value(x.elem);
		}
		else if (x.type == PrimaryExpr.character_constant)
		{
			ret.decorate(Char.instance, true, true, false);
			ret.set_value(x.elem);
		}
		else if (x.type == PrimaryExpr.real_constant)
		{
			ret.decorate(FP.instance, true, true, false);
			ret.set_value(x.elem);
		}
		else if (x.type == PrimaryExpr.string)
		{
			// As gcc doesn't consider "abcd"[2] as a constant,
			// I set this expression's 'isCosnt' flag to false
			ret.decorate(new Pointer(Char.instance), false, true, false);
			ret.set_value(x.elem);
		}
		else if (x.type == PrimaryExpr.paren_expr)
		{
			CommaExp ce = parseExpression((Expression) x.elem, y);

			// GCC doesn't consider (a, b, c) being assignable,
			// so I set this expression's 'isLval' flag to false
			ret.decorate(ce.type, ce.isConst, ce.hasInitialized, false);
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
