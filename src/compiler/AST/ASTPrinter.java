package compiler.AST;

import java.util.*;

public class ASTPrinter implements ASTNodeVisitor
{
	private static final String leading = "--".intern();
	private static final String separator = "    |".intern();

	/* prog */

	public void visit(Prog x) throws Exception
	{
		// construct sub-nodes
		// and count lines
		int lc = 1;
		ListIterator<Dec> lit = x.global_decl.listIterator();
		while (lit.hasNext())
			lit.next().accept(this);
		while (lit.hasPrevious())
			lc += lit.previous().ast_rep.length;

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = "Program".intern();

		// since no leading characters for "Program"
		// just take 2 place-holder
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = "  |".intern();

		// add sub-nodes' content
		int cl = 1;
		while (lit.hasNext())
		{
			for (String str : lit.next().ast_rep)
				x.ast_rep[cl++] += str;
		}
	}

	public void visit(VarDec x) throws Exception
	{
		// construct components and count lines
		int lc = 1;

		decl.ts.accept(this);
		lc += decl.ts.ast_rep.length;

		if (decl.init_declarator_list != null)
		{
			decl.init_declarator_list.accept(this);
			lc += decl.init_declarator_list.ast_rep.length;
		}

		// initialize format
		decl.ast_rep = new String[lc];
		decl.ast_rep[0] = leading + "Declaration".intern();
		for (int i = 1; i < lc; i++)
			decl.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		for (String str : decl.ts.ast_rep)
			decl.ast_rep[cl++] += str;

		if (decl.init_declarator_list != null)
			for (String str : decl.init_declarator_list.ast_rep)
				decl.ast_rep[cl++] += str;
	}

	public void visit(FuncDec x) throws Exception
	{
		// construct sub-nodes
		func.ts.accept(this);

		func.pd.accept(this);

		if (func.pm != null)
			func.pm.accept(this);

		func.cst.accept(this);

		// count length and initialize format
		int lc = 1;
		lc += func.ts.ast_rep.length;
		lc += func.pd.ast_rep.length;
		if (func.pm != null)
			lc += func.pm.ast_rep.length;
		lc += func.cst.ast_rep.length;

		func.ast_rep = new String[lc];
		func.ast_rep[0] = leading + "FuncDef";

		for (int i = 1; i < lc; i++)
			func.ast_rep[i] = separator;

		// add nodes' content
		int cl = 1;
		for (String str : func.ts.ast_rep)
			func.ast_rep[cl++] += str;

		for (String str : func.pd.ast_rep)
			func.ast_rep[cl++] += str;

		if (func.pm != null)
			for (String str : func.pm.ast_rep)
				func.ast_rep[cl++] += str;

		for (String str : func.cst.ast_rep)
			func.ast_rep[cl++] += str;
	}

	/* Expr */
	public void visit(CommaExp x) throws Exception
	{
		// construct nodes and count lines
		int lc = 1;
		ListIterator<AssignExp> lit = x.exp.listIterator();
		while (lit.hasNext())
		{
			AssignExp ae = lit.next();
			ae.accept(this);
			lc += ae.ast_rep.length;
		}

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "Expression".intern();
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		lit = x.exp.listIterator();
		while (lit.hasNext())
		{
			AssignExp ae = lit.next();
			for (String str : ae.ast_rep)
				x.ast_rep[cl++] += str;
		}
	}

	public void visit(AssignExp x) throws Exception
	{
		// construct nodes and count lines
		int lc = 2;
		x.left.accept(this);
		lc += x.left.ast_rep.length;
		x.right.accept(this);
		lc += x.right.ast_rep.length;

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "AssignExp";
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add sub-nodes' content
		int cl = 2;
		x.ast_rep[1] += leading + "Operator: ".intern() + x.assign_symbol();
		for (String str : x.left.ast_rep)
			x.ast_rep[cl++] += str;
		for (String str : x.right.ast_rep)
			x.ast_rep[cl++] += str;
	}

	public void visit(BinaryExp be) throws Exception
	{
		// construct sub-nodes and count lines
		int lc = 2;
		be.left.accept(this);
		lc += be.left.ast_rep.length;
		be.right.accept(this);
		lc += be.right.ast_rep.length;

		// initialize format
		be.ast_rep = new String[lc];
		be.ast_rep[0] = leading + "BinaryExp".intern();
		for (int i = 1; i < lc; i++)
			be.ast_rep[i] = separator;

		// add sub-nodes' content
		int cl = 2;
		be.ast_rep[1] += leading + "Operator: ".intern() + be.bin_symbol();
		for (String str : be.left.ast_rep)
			be.ast_rep[cl++] += str;
		for (String str : be.right.ast_rep)
			be.ast_rep[cl++] += str;
	}

	public void visit(CastExp ce) throws Exception
	{
		// construct sub-nodes
		// and count lines
		int lc = 2;
		ce.exp.accept(this);
		lc += ce.exp.ast_rep.length;

		// initialize format
		ce.ast_rep = new String[lc];
		ce.ast_rep[0] = leading + "CastExp".intern();
		for (int i = 2; i < lc; i++)
			ce.ast_rep[i] = separator;

		// add sub-nodes' content
		int cl = 1;
		for (String str : ce.target_type.ast_rep)
			ce.ast_rep[cl++] += str;
		for (String str : ce.exp.ast_rep)
			ce.ast_rep[cl++] += str;
	}

	public void visit(UnaryExp ue) throws Exception
	{
		// construct sub-nodes
		// and count lines
		int lc = 2;
		if (ue.exp != null)
		{
			ue.exp.accept(this);
			lc += ue.exp.ast_rep.length;
		}

		if (ue.type_name != null)
		{
			ue.type_name.accept(this);
			lc += ue.type_name.ast_rep.length;
		}

		// initialize format
		ue.ast_rep = new String[lc];
		ue.ast_rep[0] = leading + "UnaryExp".intern();
		for (int i = 1; i < lc; i++)
			ue.ast_rep[i] = separator;

		// add sub-nodes' content
		ue.ast_rep[1] += leading + "Operator: ".intern();

		int cl = 2;
		if (ue.exp != null)
			for (String str : ue.exp.ast_rep)
				ue.ast_rep[cl++] += str;

		if (ue.type_name != null)
			for (String str : ue.type_name.ast_rep)
				ue.ast_rep[cl++] += str;
	}

	public void visit(PostfixExp pe) throws Exception
	{
		// construct and count
		int lc = 2;
		pe.expr.accept(this);
		lc += pe.expr.ast_rep.length;

		if (pe.param != null)
		{
			if (pe.op == PostfixExpr.Operator.MPAREN)
			{
				Expression es = (Expression) pe.param;
				es.accept(this);
				lc += es.ast_rep.length;
			}
			else if (pe.op == PostfixExpr.Operator.PAREN)
			{
				Arguments al = (Arguments) pe.param;
				al.accept(this);
				lc += al.ast_rep.length;
			}
			else
				lc += 1;
		}

		// initialize format
		pe.ast_rep = new String[lc];
		pe.ast_rep[0] = leading + "PostfixExp".intern();
		for (int i = 1; i < lc; i++)
			pe.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		for (String str : pe.expr.ast_rep)
			pe.ast_rep[cl++] += str;

		pe.ast_rep[cl] += leading + "Operator: ";
		switch (pe.op)
		{
		case MPAREN:
			pe.ast_rep[cl++] += "[]";
			break;
		case PAREN:
			pe.ast_rep[cl++] += "()";
			break;
		case DOT:
			pe.ast_rep[cl++] += ".";
			break;
		case PTR:
			pe.ast_rep[cl++] += "->";
			break;
		case INC:
			pe.ast_rep[cl++] += "++";
			break;
		case DEC:
			pe.ast_rep[1] += "--";
			break;
		default:
			pe.ast_rep[1] += "";
			break;
		}

		if (pe.param != null)
		{
			if (pe.op == PostfixExpr.Operator.MPAREN)
			{
				Expression es = (Expression) pe.param;
				for (String str : es.ast_rep)
					pe.ast_rep[cl++] += str;
			}
			else if (pe.op == PostfixExpr.Operator.PAREN)
			{
				Arguments al = (Arguments) pe.param;
				for (String str : al.ast_rep)
					pe.ast_rep[cl++] += str;
			}
			else
			{
				String str = (String) pe.param;
				pe.ast_rep[cl] += leading + "Identifier: ";
				pe.ast_rep[cl++] += str;
			}
		}
	}

	public void visit(PrimaryExp pe) throws Exception
	{
		switch (pe.elem_type)
		{
		case ID:
			pe.ast_rep = new String[1];
			pe.ast_rep[0] = leading;
			pe.ast_rep[0] += "Identifier: " + (String) pe.elem;
			break;
		case STRING:
			pe.ast_rep = new String[1];
			pe.ast_rep[0] = leading;
			pe.ast_rep[0] += "String: " + '\"' + (String) pe.elem + '\"';
			break;
		case INT:
			pe.ast_rep = new String[1];
			pe.ast_rep[0] = leading;
			pe.ast_rep[0] += "Integer-Constant: " + ((Integer) pe.elem).toString();
			break;
		case CHAR:
			pe.ast_rep = new String[1];
			pe.ast_rep[0] = leading;
			pe.ast_rep[0] += "Character-Constant: " + '\'' + (Character) pe.elem + '\'';
			break;
		case PAREN:
			Expression e = (Expression) pe.elem;
			e.accept(this);
			pe.ast_rep = new String[e.ast_rep.length];
			int cl = 0;
			for (String str : e.ast_rep)
				pe.ast_rep[cl++] = str;
			break;
		}
	}

	/* Stmt */
	public void visit(ExpressionStatement es) throws Exception
	{
		if (es.e != null)
		{
			es.e.accept(this);
			es.ast_rep = es.e.ast_rep;
		}
	}

	public void visit(CompoundStatement cs) throws Exception
	{
		// construct components and count lines
		int lc = 1;
		if (cs.declaration_list != null)
		{
			cs.declaration_list.accept(this);
			lc += cs.declaration_list.ast_rep.length;
		}

		if (cs.stmt_list != null)
		{
			cs.stmt_list.accept(this);
			lc += cs.stmt_list.ast_rep.length;
		}

		// initialize format
		cs.ast_rep = new String[lc];
		cs.ast_rep[0] = leading + "CompoundStmt";
		for (int i = 1; i < lc; i++)
			cs.ast_rep[i] = separator;

		// add contents
		int cl = 1;

		if (cs.declaration_list != null)
			for (String str : cs.declaration_list.ast_rep)
				cs.ast_rep[cl++] += str;

		if (cs.stmt_list != null)
			for (String str : cs.stmt_list.ast_rep)
				cs.ast_rep[cl++] += str;
	}

	public void visit(SelectionStatement ss) throws Exception
	{
		// construct sub-nodes and count lines
		int lc = 1;

		ss.cond.accept(this);
		lc += ss.cond.ast_rep.length;

		ss.if_clause.accept(this);
		lc += ss.if_clause.ast_rep.length;

		if (ss.else_clause != null)
		{
			ss.else_clause.accept(this);
			lc += ss.else_clause.ast_rep.length;
		}

		// initialize format
		ss.ast_rep = new String[lc];
		ss.ast_rep[0] = leading + "SelectionStmt";
		for (int i = 1; i < lc; i++)
			ss.ast_rep[i] = separator;

		// add contents
		int cl = 1;

		for (String str : ss.cond.ast_rep)
			ss.ast_rep[cl++] += str;

		for (String str : ss.if_clause.ast_rep)
			ss.ast_rep[cl++] += str;

		if (ss.else_clause != null)
			for (String str : ss.else_clause.ast_rep)
				ss.ast_rep[cl++] += str;
	}

	public void visit(JumpStmt js) throws Exception
	{
		// construct sub-nodes and count lines
		int lc = 1;
		if (js.expr != null)
		{
			js.expr.accept(this);
			lc += js.expr.ast_rep.length;
		}

		js.ast_rep = new String[lc];

		// initialize format
		js.ast_rep[0] = leading + "JumpStmt: ";
		for (int i = 1; i < lc; i++)
			js.ast_rep[i] = separator;

		// add components
		switch (js.type)
		{
		case CONTINUE:
			js.ast_rep[0] += "CONTINUE";
			break;
		case BREAK:
			js.ast_rep[0] += "BREAK";
			break;
		case RETURN:
			js.ast_rep[0] += "RETURN";
			break;
		default:
			break;
		}

		if (js.expr != null)
		{
			int cl = 1;
			for (String str : js.expr.ast_rep)
				js.ast_rep[cl++] += str;
		}
	}

	public void visit(IterationStatement is) throws Exception
	{
		// construct sub-nodes and count lines
		int lc = 2;

		if (is.init != null)
		{
			is.init.accept(this);
			lc += is.init.ast_rep.length;
		}

		if (is.judge != null)
		{
			is.judge.accept(this);
			lc += is.judge.ast_rep.length;
		}

		if (is.next != null)
		{
			is.next.accept(this);
			lc += is.next.ast_rep.length;
		}

		is.stmt.accept(this);
		lc += is.stmt.ast_rep.length;

		// initialize format
		is.ast_rep = new String[lc];
		is.ast_rep[0] = leading + "IterationStmt";
		for (int i = 1; i < lc; i++)
			is.ast_rep[i] = separator;

		// add contents
		is.ast_rep[1] += leading + "Type: ";
		switch (is.iteration_type)
		{
		case FOR:
			is.ast_rep[1] += "FOR_";
			break;
		case WHILE:
			is.ast_rep[1] += "WHILE";
			break;
		default:
			break;
		}

		int cl = 2;
		if (is.iteration_type == IterationStmt.IterationStatement.WHILE)
		{
			for (String str : is.judge.ast_rep)
				is.ast_rep[cl++] += str;

			for (String str : is.stmt.ast_rep)
				is.ast_rep[cl++] += str;
		}
		else
		{
			if (is.init != null)
			{
				is.ast_rep[1] += 'O';

				for (String str : is.init.ast_rep)
					is.ast_rep[cl++] += str;
			}
			else
				is.ast_rep[1] += 'X';

			if (is.judge != null)
			{
				is.ast_rep[1] += 'O';

				for (String str : is.judge.ast_rep)
					is.ast_rep[cl++] += str;
			}
			else
				is.ast_rep[1] += 'X';

			if (is.next != null)
			{
				is.ast_rep[1] += 'O';

				for (String str : is.next.ast_rep)
					is.ast_rep[cl++] += str;
			}
			else
				is.ast_rep[1] += 'X';

			for (String str : is.stmt.ast_rep)
				is.ast_rep[cl++] += str;
		}
	}

	/* Decl */

	public void visit(Declarator vd) throws Exception
	{
		// construct components and count lines
		vd.plain_declarator.accept(this);

		if (vd.dimension.size() != 0)
		{
			Iterator<Exp> it = vd.dimension.iterator();
			while (it.hasNext())
				it.next().accept(this);
		}

		// count lines
		int lc = 1;
		lc += vd.plain_declarator.ast_rep.length;
		if (vd.dimension.size() != 0)
		{
			Iterator<Exp> it = vd.dimension.iterator();
			while (it.hasNext())
				lc += it.next().ast_rep.length;
		}

		// initialize format
		vd.ast_rep = new String[lc];
		vd.ast_rep[0] = "--VarDecl";
		for (int i = 1; i < lc; i++)
			vd.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		for (String str : vd.plain_declarator.ast_rep)
			vd.ast_rep[cl++] += str;

		if (vd.dimension.size() != 0)
		{
			Iterator<Exp> it = vd.dimension.iterator();
			while (it.hasNext())
			{
				String[] _s = it.next().ast_rep;
				for (String str : _s)
					vd.ast_rep[cl++] += str;
			}
		}
	}

	public void visit(DeclarationList ds) throws Exception
	{
		// construct components
		DeclarationList _dl = ds;
		while (_dl != null)
		{
			_dl.head.accept(this);
			_dl = _dl.next;
		}

		// count lines
		int lc = 1;
		_dl = ds;
		while (_dl != null)
		{
			lc += _dl.head.ast_rep.length;
			_dl = _dl.next;
		}

		// initialize format
		ds.ast_rep = new String[lc];
		ds.ast_rep[0] = leading + "DeclarationList";
		for (int i = 1; i < lc; i++)
			ds.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		_dl = ds;
		while (_dl != null)
		{
			for (String str : _dl.head.ast_rep)
				ds.ast_rep[cl++] += str;

			_dl = _dl.next;
		}
	}

	public void visit(DeclaratorList ds) throws Exception
	{
		// construct components
		DeclaratorList _dl = ds;
		while (_dl != null)
		{
			_dl.head.accept(this);
			_dl = _dl.next;
		}

		// count lines
		int lc = 1;
		_dl = ds;
		while (_dl != null)
		{
			lc += _dl.head.ast_rep.length;
			_dl = _dl.next;
		}

		// initialize format
		ds.ast_rep = new String[lc];
		ds.ast_rep[0] = leading + "DeclaratorList";
		for (int i = 1; i < lc; i++)
			ds.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		_dl = ds;
		while (_dl != null)
		{
			for (String str : _dl.head.ast_rep)
				ds.ast_rep[cl++] += str;

			_dl = _dl.next;
		}
	}

	public void visit(InitDeclarator id) throws Exception
	{
		// construct components and count lines
		int lc = 1;

		id.declarator.accept(this);
		lc += id.declarator.ast_rep.length;

		if (id.initializer != null)
		{
			id.initializer.accept(this);
			lc += id.initializer.ast_rep.length;
		}

		// initialize format
		id.ast_rep = new String[lc];
		id.ast_rep[0] = leading + "InitDeclarator";
		for (int i = 1; i < lc; i++)
			id.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		for (String str : id.declarator.ast_rep)
			id.ast_rep[cl++] += str;

		if (id.initializer != null)
			for (String str : id.initializer.ast_rep)
				id.ast_rep[cl++] += str;
	}

	public void visit(InitDeclarators ids) throws Exception
	{
		// construct components
		InitDeclarators _dl = ids;
		while (_dl != null)
		{
			_dl.head.accept(this);
			_dl = _dl.next;
		}

		// count lines
		int lc = 1;
		_dl = ids;
		while (_dl != null)
		{
			lc += _dl.head.ast_rep.length;
			_dl = _dl.next;
		}

		// initialize format
		ids.ast_rep = new String[lc];
		ids.ast_rep[0] = leading + "InitDeclaratorList";
		for (int i = 1; i < lc; i++)
			ids.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		_dl = ids;
		while (_dl != null)
		{
			for (String str : _dl.head.ast_rep)
				ids.ast_rep[cl++] += str;

			_dl = _dl.next;
		}
	}

	public void visit(Initializer ini) throws Exception
	{
		// construct components and count lines
		int lc = 1;

		if (ini.expr != null)
		{
			ini.expr.accept(this);
			lc += ini.expr.ast_rep.length;
		}

		if (ini.initializer_list != null)
		{
			ini.initializer_list.accept(this);
			lc += ini.initializer_list.ast_rep.length;
		}

		// initialize format
		ini.ast_rep = new String[lc];
		ini.ast_rep[0] = leading + "Initializer";
		for (int i = 1; i < lc; i++)
			ini.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		if (ini.expr != null)
			for (String str : ini.expr.ast_rep)
				ini.ast_rep[cl++] += str;

		if (ini.initializer_list != null)
			for (String str : ini.initializer_list.ast_rep)
				ini.ast_rep[cl++] += str;
	}

	public void visit(InitializerList x) throws Exception
	{
		// construct components
		InitializerList y = x;
		while (y != null)
		{
			y.head.accept(this);
			y = y.next;
		}

		// count lines
		int lc = 1;
		y = x;
		while (y != null)
		{
			lc += y.head.ast_rep.length;
			y = y.next;
		}

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "InitializerList";
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		y = x;
		while (y != null)
		{
			for (String str : y.head.ast_rep)
				x.ast_rep[cl++] += str;

			y = y.next;
		}
	}

	public void visit(NonInitDeclaration x) throws Exception
	{
		// construct components and count lines
		int lc = 1;
		x.type_specifier.accept(this);
		lc += x.type_specifier.ast_rep.length;

		x.declarator_list.accept(this);
		lc += x.declarator_list.ast_rep.length;

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "NonInitDeclaration";
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		for (String str : x.type_specifier.ast_rep)
			x.ast_rep[cl++] += str;

		for (String str : x.declarator_list.ast_rep)
			x.ast_rep[cl++] += str;
	}

	public void visit(NonInitDeclarationList x) throws Exception
	{
		// construct components
		NonInitDeclarationList y = x;
		while (y != null)
		{
			y.head.accept(this);
			y = y.next;
		}

		// count lines
		int lc = 1;
		y = x;
		while (y != null)
		{
			lc += y.head.ast_rep.length;
			y = y.next;
		}

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "NonInitDeclarationList";
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		y = x;
		while (y != null)
		{
			for (String str : y.head.ast_rep)
				x.ast_rep[cl++] += str;

			y = y.next;
		}
	}

	public void visit(PlainDeclaration x) throws Exception
	{
		// construct components and count lines
		int lc = 1;
		x.ts.accept(this);
		lc += x.ts.ast_rep.length;

		x.dlr.accept(this);
		lc += x.dlr.ast_rep.length;

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "PlainDeclaration";
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		for (String str : x.ts.ast_rep)
			x.ast_rep[cl++] += str;

		for (String str : x.dlr.ast_rep)
			x.ast_rep[cl++] += str;
	}

	public void visit(PlainDeclarator x) throws Exception
	{
		// construct components and count lines
		int lc = 2;
		if (x.star_list.cnt > 0)
		{
			x.star_list.accept(this);
			lc += x.star_list.ast_rep.length;
		}

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "PlainDeclarator";
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		if (x.star_list.cnt > 0)
			for (String str : x.star_list.ast_rep)
				x.ast_rep[cl++] += str;

		x.ast_rep[cl++] += leading + "Identifier: " + x.name;
	}

	public void visit(Arguments x) throws Exception
	{
		// construct components
		Arguments y = x;
		while (y != null)
		{
			y.head.accept(this);
			y = y.next;
		}

		// count lines
		int lc = 1;
		y = x;
		while (y != null)
		{
			lc += y.head.ast_rep.length;
			y = y.next;
		}

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "ArgumentList";
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		y = x;
		while (y != null)
		{
			for (String str : y.head.ast_rep)
				x.ast_rep[cl++] += str;

			y = y.next;
		}
	}

	public void visit(ParameterList x) throws Exception
	{
		// construct components
		ParameterList y = x;
		while (y != null)
		{
			y.head.accept(this);
			y = y.next;
		}

		// count lines
		int lc = 1;
		y = x;
		while (y != null)
		{
			lc += y.head.ast_rep.length;
			y = y.next;
		}

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "ParameterList";
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		y = x;
		while (y != null)
		{
			for (String str : y.head.ast_rep)
				x.ast_rep[cl++] += str;

			y = y.next;
		}
	}

	/* Type */
	public void visit(TypeName x) throws Exception
	{
		// construct components and count lines
		int lc = 1;
		x.type_specifier.accept(this);
		lc += x.type_specifier.ast_rep.length;

		x.star_list.accept(this);
		lc += x.star_list.ast_rep.length;

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "TypeName";
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		for (String str : x.type_specifier.ast_rep)
			x.ast_rep[cl++] += str;

		for (String str : x.star_list.ast_rep)
			x.ast_rep[cl++] += str;
	}

	public void visit(TypeSpecifier ts) throws Exception
	{
		// construct sub-nodes
		if (ts.comp != null)
			ts.comp.accept(this);

		// count lines
		int lc = 2;
		if (ts.comp != null)
			lc += ts.comp.ast_rep.length;

		// initialize format
		ts.ast_rep = new String[lc];
		ts.ast_rep[0] = leading + "TypeSpecifier";
		for (int i = 1; i < lc; i++)
			ts.ast_rep[i] = separator;

		// add contents
		ts.ast_rep[1] += leading + "Type: ";
		switch (ts.type_detail)
		{
		case VOID:
			ts.ast_rep[1] += "void";
			break;
		case INT:
			ts.ast_rep[1] += "int";
			break;
		case CHAR:
			ts.ast_rep[1] += "char";
			break;
		case STRUCT:
			ts.ast_rep[1] += "struct ";
			break;
		case UNION:
			ts.ast_rep[1] += "union ";
			break;
		}
		if (ts.tag != null)
			ts.ast_rep[1] += ts.tag;

		int cl = 2;
		if (ts.comp != null)
			for (String str : ts.comp.ast_rep)
				ts.ast_rep[cl++] += str;
	}

	@Override
	public void visit(ExprStmt x) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(CompStmt x) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SelectStmt x) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IterStmt x) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Init x) throws Exception
	{
		// TODO Auto-generated method stub

	}
}
