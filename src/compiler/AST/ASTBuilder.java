package compiler.AST;

import java.util.*;
import compiler.AST.FuncDec.Parameter;
import compiler.AST.PostfixExp.PostfixElem;
import compiler.Parser.*;
import compiler.Parser.PostfixExpr.Postfix;
import compiler.Scoping.*;
import compiler.Typing.*;
import compiler.Typing.Void;

public class ASTBuilder
{
	private int offset;
	private int loop_cnt;
	private Env tenv, venv;

	public ASTBuilder()
	{
		offset = 0;
		loop_cnt = 0;
		tenv = null;
		venv = null;
	}

	public Prog build(Program p) throws Exception
	{
		Prog ast = new Prog();
		tenv = ast.tenv;
		venv = ast.venv;

		// build the structure of AST
		for (ProgComp pc : p.elem)
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

		// check if there exists incomplete types
		Enumeration<Symbol> e = tenv.keys();
		while (e.hasMoreElements())
		{
			Symbol csym = e.nextElement();
			Type ct = tenv.get_local(csym).actual;

			// defensive check
			if (ct == null || !(ct instanceof Record))
				panic("Internal Error.");

			if (!check_complete(ct))
				panic("Incomplete type detected.");
		}

		return ast;
	}

	private void parseDeclaration(Declaration x, Env y, Prog z) throws Exception
	{
		Type def_type = parseTypeSpecifier(x.ts, y);
		if (x.elem.isEmpty())
		{
			if (def_type instanceof Void || Type.numeric(def_type))
				panic("Meaningless declaration of intrinsic type!");

			if (def_type instanceof Record)
			{
				Record st = (Record) def_type;
				if (st.field.isEmpty())
				{
					Symbol ss = Symbol.getSymbol(st.tag);
					tenv.put(ss, new Entry(def_type));
				}
			}
			else
				panic("Internal Error.");
		}
		else
		{
			for (InitDeclarator idr : x.elem)
			{
				// Name and Symbol
				String var_name = idr.declarator.plain_declarator.name;
				Symbol var_sym = Symbol.getSymbol(var_name);

				// Check if has been declared
				if (y.get_local(var_sym) != null)
					panic("Variable: " + var_name + " has been declared in this scope!");

				// Variable type
				Type var_type = resolve_type(def_type, idr.declarator, y);

				// 'void' is incomplete
				if (var_type instanceof Void)
					panic("Variable has incomplete type \'void\'.");

				// Initializer
				boolean hasInit = idr.initializer != null;
				Init initializer = hasInit ? parseInitializer(idr.initializer, y) : null;

				// Check if the initialization is proper
				if (hasInit && !initializer.check_init(var_type))
					panic("Invalid initialization.");

				// ASTNode
				VarDec var_dec = new VarDec(var_type, var_name, initializer, offset);
				offset += var_type.width;
				z.add_dec(var_dec);

				// Environment
				Entry var_entry = new Entry(var_dec);
				y.put(var_sym, var_entry);
			}
		}
	}

	private void parseFuncDef(FuncDef x, Env y, Prog z) throws Exception
	{
		// Name and Symbol
		String func_name = x.pd.name;
		Symbol func_sym = Symbol.getSymbol(func_name);

		// Check if the function has been declared
		if (y.get_local(func_sym) != null)
			panic("Function: " + func_name + " has already been declared!");

		// Return type
		Type def_type = parseTypeSpecifier(x.ts, y);
		Type ret_type = resolve_plain_type(def_type, x.pd);

		// ASTNode
		FuncDec func_dec = new FuncDec(ret_type, func_name, offset);
		func_dec.scope = new Env(y);

		// handle parameters
		for (PlainDeclaration pdn : x.pm)
		{
			String param_name = pdn.dlr.plain_declarator.name;
			Symbol param_sym = Symbol.getSymbol(param_name);

			if (func_dec.scope.get_local(param_sym) == null)
			{
				Type param_type = parsePlainDeclaration(pdn, func_dec.scope);
				func_dec.add_param(param_name, param_type);

				VarDec param_dec = new VarDec(param_type, param_name, null, offset);
				offset += param_type.width;
				param_dec.hasAssigned = true;
				func_dec.add_var(param_dec);

				Entry param_entry = new Entry(param_dec);
				func_dec.scope.put(param_sym, param_entry);
			}
			else
				panic("Variable \'" + param_name + "\' has been declared.");
		}

		// function body
		CompoundStatement tcst = x.cst;
		for (Declaration decl : tcst.decls)
		{
			Type tdtp = parseTypeSpecifier(decl.ts, y);
			for (InitDeclarator idr : decl.elem)
			{
				String var_name = idr.declarator.plain_declarator.name;
				Symbol var_sym = Symbol.getSymbol(var_name);

				// check duplication
				if (func_dec.scope.get_local(var_sym) != null)
					panic("Variable " + var_name + " has been declared in this scope.");

				Type tvtp = resolve_type(tdtp, idr.declarator, func_dec.scope);
				boolean hasInit = idr.initializer != null;
				Init var_it = hasInit ? parseInitializer(idr.initializer, func_dec.scope) : null;
				VarDec var_dec = new VarDec(tvtp, var_name, var_it, offset);
				offset += tvtp.width;
				func_dec.add_var(var_dec);
				Entry var_entry = new Entry(Entry.entry_var, var_dec);
				func_dec.scope.put(var_sym, var_entry);
			}
		}

		for (Statement st : tcst.stmts)
		{
			Stmt s = parseStatement(st, func_dec.scope);

			// check return type
			if (s instanceof JumpStmt)
			{
				JumpStmt jst = (JumpStmt) s;
				if (jst.category == JumpStmt.jp_ret && jst.expr != null)
				{
					CommaExp frt = jst.expr;
					if (!frt.type.isConvertableTo(ret_type))
						panic("Invalid return type.");
				}
			}

			func_dec.add_stmt(s);
		}

		// AST hierarchy
		z.add_dec(func_dec);

		// Environment
		Function func_type = new Function(Void.instance, ret_type);
		ListIterator<Parameter> lit = func_dec.param.listIterator(func_dec.param.size());
		while (lit.hasPrevious())
		{
			Type ct = lit.previous().type;
			func_type = new Function(ct, func_type);
		}
		Entry func_entry = new Entry(func_dec, func_type);
		y.put(func_sym, func_entry);
	}

	private Type parseTypeSpecifier(TypeSpecifier t, Env y) throws Exception
	{
		if (t.ts_type == TypeSpecifier.ts_void)
			return Void.instance;
		else if (t.ts_type == TypeSpecifier.ts_int)
			return Int.getInstance();
		else if (t.ts_type == TypeSpecifier.ts_char)
			return Char.instance;
		else if (t.ts_type == TypeSpecifier.ts_float || t.ts_type == TypeSpecifier.ts_double)
			return FP.instance;
		else if (t.ts_type == TypeSpecifier.ts_struct)
		{
			String tag = t.name;
			if (t.entry.isEmpty()) // type-specifier ::= 'struct' identifier
			{
				// defensive check
				if (tag == null)
					panic("Internal Error.");

				// get symbol from tag-environment
				Symbol ss = Symbol.getSymbol(tag);
				Entry ste = tenv.get_local(ss);

				if (ste != null)
				{
					// has been declared, just check type
					if (!(ste.actual instanceof Struct))
						panic(tag + " is not declared as struct!");

					return ste.actual;
				}
				else
				{
					// maybe just declaration, maybe undefined usage, need to check further
					Struct ret = new Struct();
					ret.set_tag(tag);
					return ret;
				}
			}
			else
			{
				Struct ret = null;
				if (tag != null) // type-specifier ::= 'struct' identifier { ... }
				{
					Symbol tag_sym = Symbol.getSymbol(tag);
					Entry ce = tenv.get_local(tag_sym);

					if (ce != null)// Has been declared before
					{
						if (!(ce.actual instanceof Struct))
							panic(tag + " is not declared as struct!");

						ret = (Struct) ce.actual;
						if (ret.field.size() == 0)
							panic("struct " + tag + " has been defined before!");
					}
					else // First time meet
					{
						ret = new Struct();
						ret.set_tag(tag);
						tenv.put(tag_sym, new Entry(ret));
					}
				}

				// type-specifier ::= 'struct' { ... }
				// only add entries
				for (RecordEntry re : t.entry)
				{
					Type dt = parseTypeSpecifier(re.ts, y);
					for (Declarator dclr : re.dls)
					{
						String name = dclr.plain_declarator.name;
						if (ret.field.containsKey(name))
							panic("Variable \'" + name + "\' in current tag scope has been defined.");

						Type ct = resolve_type(dt, dclr, y);
						ret.add_record(name, ct);
					}
				}

				return ret;
			}
		}
		else if (t.ts_type == TypeSpecifier.ts_union)
		{
			String tag = t.name;
			if (t.entry.isEmpty()) // type-specifier ::= 'union' identifier
			{
				// defensive check
				if (tag == null)
					panic("Internal Error.");

				// get symbol from tag-environment
				Symbol ss = Symbol.getSymbol(tag);
				Entry ste = tenv.get_local(ss);

				if (ste != null)
				{
					// has been declared, just check type
					if (!(ste.actual instanceof Union))
						panic(tag + " is not declared as union!");

					return ste.actual;
				}
				else
				{
					// maybe just declaration, maybe undefined usage, need to check further
					Union ret = new Union();
					ret.set_tag(tag);
					return ret;
				}
			}
			else
			{
				Union ret = null;

				if (tag != null) // type-specifier ::= 'union' identifier { ... }
				{
					Symbol tag_sym = Symbol.getSymbol(tag);
					Entry ce = tenv.get_local(tag_sym);

					if (ce != null)// Has been declared before
					{
						if (!(ce.actual instanceof Union))
							panic(tag + " is not declared as union!");

						ret = (Union) ce.actual;
						if (ret.field.size() == 0)
							panic("union " + tag + " has been defined before!");
					}
					else // First time meet
					{
						ret = new Union();
						ret.set_tag(tag);
						tenv.put(tag_sym, new Entry(ret));
					}
				}

				// type-specifier ::= 'union' { ... } also applys
				// only add entries
				for (RecordEntry re : t.entry)
				{
					Type dt = parseTypeSpecifier(re.ts, y);
					for (Declarator dclr : re.dls)
					{
						String name = dclr.plain_declarator.name;
						if (ret.field.containsKey(name))
							panic("Variable \'" + name + "\' in current tag scope has been defined.");

						Type ct = resolve_type(dt, dclr, y);
						ret.add_record(name, ct);
					}
				}

				return ret;
			}
		}
		else
		{
			panic("Internal Error.");
			return null;
		}
	}

	private Type resolve_type(Type base, Declarator dr, Env y) throws Exception
	{
		// Base type
		Type ret = resolve_plain_type(base, dr.plain_declarator);

		// Note that the order must be counted from tail to head
		ListIterator<ConstantExpr> lit = dr.dimension.listIterator(dr.dimension.size());
		while (lit.hasPrevious())
		{
			ConstantExpr ce = lit.previous();
			Object val = parseConstantExpr(ce, y);
			if (val instanceof Integer)
			{
				int cnt = ((Integer) val).intValue();
				if (cnt < 0)
					panic("Dimension must be non-negative!");

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
		Init ret = null;
		if (x.type == Initializer.assign)// x = a;
		{
			AssignExp e = parseAssignmentExpr(x.ae, y);
			if (!e.hasInitialized)
				panic("Unintialized assignment-expression can not be used as an intializer!");

			ret = new Init(e);
		}
		else if (x.type == Initializer.list)// x[3] = {a, b, c}
		{
			ret = new Init();
			for (Initializer it : x.comp)
			{
				Init cit = parseInitializer(it, y);
				ret.add_init(cit);
			}
			return ret;
		}
		else
			panic("Internal Error.");

		return ret;
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
		{
			Env.beginScope(y);
			ret = parseCompoundStmt((CompoundStatement) st, y);
			Env.endScope(y);
		}
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
				if (ret.scope.get_local(var_sym) != null)
					panic("Variable " + var_name + " has been declared in this scope.");

				Type var_type = resolve_type(def_type, idr.declarator, y);
				boolean hasInit = idr.initializer != null;
				Init var_it = hasInit ? parseInitializer(idr.initializer, y) : null;
				VarDec var_dec = new VarDec(var_type, var_name, var_it, offset);
				offset += var_type.width;
				ret.add_var(var_dec);
				Entry var_entry = new Entry(Entry.entry_var, var_dec);
				ret.scope.put(var_sym, var_entry);
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
		Stmt stf = x.else_clause != null ? parseStatement(x.else_clause, y) : null;
		return new SelectStmt(ce, stt, stf);
	}

	private IterStmt parseIterationStatement(IterationStatement x, Env y) throws Exception
	{
		IterStmt ret = null;
		if (x.type == IterationStatement.WHILE)
		{
			++loop_cnt;
			CommaExp ce = parseExpression(x.judge, y);
			Stmt st = parseStatement(x.stmt, y);
			--loop_cnt;
			ret = new IterStmt(ce, st);
		}
		else if (x.type == IterationStatement.FOR)
		{
			++loop_cnt;
			CommaExp ce1 = parseExpression(x.init, y);
			CommaExp ce2 = parseExpression(x.judge, y);
			CommaExp ce3 = parseExpression(x.next, y);
			Stmt st = parseStatement(x.stmt, y);
			--loop_cnt;
			ret = new IterStmt(ce1, ce2, ce3, st);
		}
		else
			panic("Internal Error.");

		return ret;
	}

	private JumpStmt parseJumpStatement(JumpStatement x, Env y) throws Exception
	{
		JumpStmt ret = null;
		if (x.type == JumpStatement.CTNU)
		{
			if (loop_cnt > 0)
				ret = new JumpStmt(JumpStmt.jp_ctn);
			else
				panic("Continue statement not within a loop!");
		}
		else if (x.type == JumpStatement.BRK)
		{
			if (loop_cnt > 0)
				ret = new JumpStmt(JumpStmt.jp_brk);
			else
				panic("Break statement not within a loop!");
		}
		else if (x.type == JumpStatement.RET)
		{
			if (x.expr == null)
				ret = new JumpStmt(JumpStmt.jp_ret);
			else
			{
				CommaExp ce = parseExpression(x.expr, y);
				ret = new JumpStmt(ce);
			}
		}
		else
			panic("Internal Error.");

		return ret;
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
			AssignmentExpr aer = lit.next();
			AssignExp ae = parseAssignmentExpr(aer, y);
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

		// defensive check
		if (x.rexpr == null)
			panic("Internal Error.");

		// build right expression first
		ret.right = parseLogicalOrExpr(x.rexpr, y);

		// decorate
		ret.decorate(ret.right.type, ret.right.isConst, ret.right.hasInitialized, ret.right.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.right.value);

		// defensive check
		if (x.op_list.size() != x.lexpr_list.size())
			panic("Internal Error.");

		ListIterator<Integer> alit = x.op_list.listIterator(x.op_list.size());
		ListIterator<UnaryExpr> ulit = x.lexpr_list.listIterator(x.lexpr_list.size());

		// build left side iteratively
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

		// decorate
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (clit.hasNext())
		{
			ret.op = BinaryExp.OR;
			ret.right = parseLogicalAndExpr(clit.next(), y);

			// semantic check
			// the operands to the logical OR operator need not be of the same type,
			// but they must be of integral or pointer type.
			if (!Type.logic(ret.left.type) || !Type.logic(ret.right.type))
				panic("Invalid operand.");

			// decorate
			boolean icons = ret.left.isConst && ret.right.isConst;
			boolean hinit = ret.left.hasInitialized && ret.right.hasInitialized;

			// actually the type of the expression should be of 'bool',
			// but we use 'int' instead for simplicity
			ret.decorate(Int.getInstance(), icons, hinit, false);
			if (ret.isConst)
				ret.calc_const_val();

			// build tree
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

		// decorate
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (clit.hasNext())
		{
			ret.op = BinaryExp.AND;
			ret.right = parseInclusiveOrExpr(clit.next(), y);

			// semantic check
			// the operands to the logical AND operator need not be of the same type,
			// but they must be of integral or pointer type.
			if (!Type.logic(ret.left.type) || !Type.logic(ret.right.type))
				panic("Invalid operand.");

			// decorate
			boolean icons = ret.left.isConst && ret.right.isConst;
			boolean hinit = ret.left.hasInitialized && ret.right.hasInitialized;

			// actually the type of the expression should be of 'bool',
			// but we use 'int' instead for simplicity
			ret.decorate(Int.getInstance(), icons, hinit, false);
			if (ret.isConst)
				ret.calc_const_val();

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

		// decorate
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (clit.hasNext())
		{
			ret.op = BinaryExp.BIT_OR;
			ret.right = parseExclusiveOrExpr(clit.next(), y);

			// the operands of bitwise operators must have integral types
			if (!Type.numeric(ret.left.type) || !Type.numeric(ret.right.type))
				panic("Invalid operand.");

			// decorate
			boolean icons = ret.left.isConst && ret.right.isConst;
			boolean hinit = ret.left.hasInitialized && ret.right.hasInitialized;

			// actually the type of the expression should be of 'bool',
			// but we use 'int' instead for simplicity
			ret.decorate(Int.getInstance(), icons, hinit, false);
			if (ret.isConst)
				ret.calc_const_val();

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

		// decorate
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (clit.hasNext())
		{
			ret.op = BinaryExp.BIT_XOR;
			ret.right = parseAndExpr(clit.next(), y);

			// the operands of bitwise operators must have integral types
			if (!Type.numeric(ret.left.type) || !Type.numeric(ret.right.type))
				panic("Invalid operand.");

			// decorate
			boolean icons = ret.left.isConst && ret.right.isConst;
			boolean hinit = ret.left.hasInitialized && ret.right.hasInitialized;

			// actually the type of the expression should be of 'bool',
			// but we use 'int' instead for simplicity
			ret.decorate(Int.getInstance(), icons, hinit, false);
			if (ret.isConst)
				ret.calc_const_val();

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

		// decorate
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (clit.hasNext())
		{
			ret.op = BinaryExp.BIT_AND;
			ret.right = parseEqualityExpr(clit.next(), y);

			// the operands of bitwise operators must have integral types
			if (!Type.numeric(ret.left.type) || !Type.numeric(ret.right.type))
				panic("Invalid operand.");

			// decorate
			boolean icons = ret.left.isConst && ret.right.isConst;
			boolean hinit = ret.left.hasInitialized && ret.right.hasInitialized;

			// actually the type of the expression should be of 'bool',
			// but we use 'int' instead for simplicity
			ret.decorate(Int.getInstance(), icons, hinit, false);
			if (ret.isConst)
				ret.calc_const_val();

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

			// semantic check
			if (!Type.arith(ret.left.type) || !Type.arith(ret.right.type))
				panic("Invalid operand.");

			// decorate
			boolean icons = ret.left.isConst && ret.right.isConst;
			boolean hinit = ret.left.hasInitialized && ret.right.hasInitialized;

			// actually the type of the expression should be of 'bool',
			// but we use 'int' instead for simplicity
			ret.decorate(Int.getInstance(), icons, hinit, false);
			if (ret.isConst)
				ret.calc_const_val();

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

		// decorate
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (plit.hasNext())
		{
			ret.op = plit.next().intValue();
			ret.right = parseShiftExpr(clit.next(), y);

			// semantic check
			if (!Type.arith(ret.left.type) || !Type.arith(ret.right.type))
				panic("Invalid operand.");

			// decorate
			boolean icons = ret.left.isConst && ret.right.isConst;
			boolean hinit = ret.left.hasInitialized && ret.right.hasInitialized;

			// actually the type of the expression should be of 'bool',
			// but we use 'int' instead for simplicity
			ret.decorate(Int.getInstance(), icons, hinit, false);
			if (ret.isConst)
				ret.calc_const_val();

			// build tree
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

		// decorate
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (plit.hasNext())
		{
			ret.op = plit.next().intValue();
			ret.right = parseAdditiveExpr(clit.next(), y);

			// semantic check
			// both operands must be integral values.
			if (!Type.integer(ret.left.type) || !Type.integer(ret.right.type))
				panic("Invalid operand.");

			boolean icons = ret.left.isConst && ret.right.isConst;
			boolean hinit = ret.left.hasInitialized && ret.right.hasInitialized;

			// the type of the result is the type of the left operand after conversion
			ret.decorate(ret.left.type, icons, hinit, false);
			if (ret.isConst)
				ret.calc_const_val();

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

		// decorate
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (plit.hasNext())
		{
			// build ast
			ret.op = plit.next().intValue();
			ret.right = parseMultiplicativeExpr(clit.next(), y);

			// semantic check
			if (!Type.arith(ret.left.type) || !Type.arith(ret.right.type))
				panic("Invalid operand");

			// decorate
			boolean icons = ret.left.isConst && ret.right.isConst;
			boolean hinit = ret.left.hasInitialized && ret.right.hasInitialized;
			ret.decorate(Type.max(ret.left.type, ret.right.type), icons, hinit, false);
			if (ret.isConst)
				ret.calc_const_val();

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

		// decorate
		ret.decorate(ret.left.type, ret.left.isConst, ret.left.hasInitialized, ret.left.isLvalue);
		if (ret.isConst)
			ret.set_value(ret.left.value);

		// leaf or node cluster
		while (plit.hasNext())
		{
			// build AST Node
			ret.op = plit.next().intValue();
			ret.right = parseCastExpr(clit.next(), y);

			// semantic check
			if (!Type.numeric(ret.left.type) || !Type.numeric(ret.right.type))
				panic("Invalid operand.");

			// decorate
			boolean icons = ret.left.isConst && ret.right.isConst;
			boolean hinit = ret.left.hasInitialized && ret.right.hasInitialized;
			ret.decorate(Type.max(ret.left.type, ret.right.type), icons, hinit, false);
			if (ret.isConst)
				ret.calc_const_val();

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

			// Semantic check
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

			// Semantic check
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
				ret.decorate(Int.getInstance(), ce.isConst, ce.hasInitialized, false);
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
				ret.decorate(Int.getInstance(), true, true, false);
				ret.set_value(ue.type.width);
			}
			else if (x.elem instanceof TypeName)
			{
				TypeName tpn = (TypeName) x.elem;
				Type t = parseTypeName(tpn, y);

				ret = new UnaryExp(UnaryExpr.sizeof, null, t);
				ret.decorate(Int.getInstance(), true, true, false);
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
		ret.decorate(cur_type, pe.isConst, pe.hasInitialized, pe.isLvalue);

		ListIterator<Postfix> lit = x.elem.listIterator();
		while (lit.hasNext())
		{
			Postfix pfx = lit.next();
			if (pfx.type == PostfixExpr.mparen)
			{
				CommaExp ce = parseExpression((Expression) pfx.content, y);
				if (ce.type instanceof Char || ce.type instanceof Int)
				{
					// a[-2] is acceptable, no need to check range
					boolean init_flag = pe.hasInitialized && ce.hasInitialized;
					if (cur_type instanceof Pointer || cur_type instanceof Array)
					{
						cur_type = cur_type instanceof Pointer ? ((Pointer) cur_type).elem_type : ((Array) cur_type).elem_type;
						ret.add_elem(PostfixElem.post_idx, ce, null, cur_type);
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

					// Function Call parameters check
					ListIterator<AssignExp> plit = ce.exp.listIterator();
					ListIterator<Type> atlit = fdatp.listIterator();
					while (plit.hasNext())
					{
						Type cptp = plit.next().type;
						Type catp = atlit.next();
						if (!cptp.isConvertableTo(catp))
							panic("Incompatible parameter type.");
					}

					cur_type = fdatp.getLast();
					ret.add_elem(PostfixElem.post_call, ce, null, cur_type);
					ret.decorate(cur_type, false, true, false);
				}
				else
					panic("Only function can be called.");
			}
			else if (pfx.type == PostfixExpr.dot)
			{
				if (cur_type instanceof Record)
				{
					// Get member type
					Record pt = (Record) cur_type;
					String member = (String) pfx.content;
					cur_type = pt.get_member_type(member);

					// Check
					if (cur_type == null)
						panic("Not a member.");

					// Decorate the exp
					ret.add_elem(PostfixElem.post_dot, null, member, cur_type);
					ret.decorate(cur_type, false, pe.hasInitialized, true);
					if (cur_type instanceof Array)
					{
						ret.isLvalue = false;
						ret.isConst = true;
					}
				}
				else
					panic("Not a struct or union.");
			}
			else if (pfx.type == PostfixExpr.ptr)
			{
				if (cur_type instanceof Pointer)
				{
					Type pt = ((Pointer) cur_type).elem_type;
					if (pt instanceof Record)
					{
						// Get member type
						Record cpt = (Record) pt;
						String member = (String) pfx.content;
						cur_type = cpt.get_member_type(member);

						// Check
						if (cur_type == null)
							panic("Not a member.");

						// Decorate the exp
						ret.add_elem(PostfixElem.post_arrow, null, member, cur_type);
						ret.decorate(cur_type, false, pe.hasInitialized, true);
						if (cur_type instanceof Array)
						{
							ret.isLvalue = false;
							ret.isConst = true;
						}
					}
					else
						panic("Not a pointer to record.");
				}
				else
					panic("Not a pointer.");
			}
			else if (pfx.type == PostfixExpr.inc || pfx.type == PostfixExpr.dec)
			{
				boolean operable = Type.numeric(cur_type) || cur_type instanceof Pointer;
				if (!operable || !ret.isLvalue)
					panic("Can not be incremented.");

				int cat = pfx.type == PostfixExpr.inc ? PostfixElem.post_inc : PostfixElem.post_dec;
				ret.add_elem(cat, null, null, cur_type);
				ret.decorate(cur_type, false, pe.hasInitialized, false);
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
			Entry entry = y.get_global(var_sym);

			if (entry == null)
				panic("Symbol \'" + var_name + "\' is undefined.");

			if (entry.type == Entry.entry_var)
			{
				VarDec vd = (VarDec) entry.mirror;
				ret.decorate(vd.type, vd.isConst, vd.isInitialized(), vd.isLval);
				if (ret.isConst)
					ret.set_value(vd.val);
			}
			else if (entry.type == Entry.entry_func)
			{
				FuncDec fd = (FuncDec) entry.mirror;
				ret.decorate(entry.actual, true, true, false);
				ret.set_value(fd);
			}
			else if (entry.type == Entry.entry_type)
				panic("Can not use a type as identifier!");
			else
				panic("Internal Error.");
		}
		else if (x.type == PrimaryExpr.integer_constant)
		{
			ret.decorate(Int.getInstance(), true, true, false);
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
			// As GCC doesn't consider "abcd"[2] as a constant,
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

	private boolean check_complete(Type tp)
	{
		if (tp.complete || !(tp instanceof Record))
			return true;

		// check if there's circular definition using BFS
		if (tp.visited)
		{
			tp.complete = false;
			return false;
		}

		tp.visited = true;
		Record rd = (Record) tp;

		for (Symbol csym : rd.field.keySet())
		{
			Type cem = rd.field.get(csym);
			if (!check_complete(cem))
			{
				cem.complete = false;
				return false;
			}
		}

		tp.complete = true;
		return true;
	}

	private void panic(String msg) throws Exception
	{
		throw new Exception(msg);
	}
}
