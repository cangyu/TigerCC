package compiler.AST;

import java.util.*;
import compiler.AST.FuncDec.Parameter;
import compiler.AST.PostfixExp.PostfixElem;
import compiler.Lexer.Token;
import compiler.Typing.*;

public class ASTPrinter implements ASTNodeVisitor
{
	private static final String leading = "--".intern();
	private static final String separator = "    |".intern();

	/* prog */
	public void visit(Prog x) throws Exception
	{
		// construct sub-nodes and count lines
		int lc = 1;
		ListIterator<Dec> lit = x.general_decl.listIterator();
		while (lit.hasNext())
		{
			Dec cdc = lit.next();
			cdc.accept(this);
			lc += cdc.ast_rep.length;
		}

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = "Program".intern();

		// since no leading characters for "Program", just take 2 place-holder
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = "  |".intern();

		// add sub-nodes' content
		int cl = 1;
		lit = x.general_decl.listIterator();
		while (lit.hasNext())
		{
			Dec cdc = lit.next();
			for (String str : cdc.ast_rep)
				x.ast_rep[cl++] += str;
		}
	}

	/* Dec */
	public void visit(VarDec x) throws Exception
	{
		// construct components
		boolean hasInit = x.init != null;
		if (hasInit)
			x.init.accept(this);

		// count lines
		int lc = hasInit ? 2 : 1;

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "Var: ".intern() + x.name + " -> ".intern() + x.type.toString();
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		if (hasInit)
			x.ast_rep[cl++] += "InitVal: ".intern() + x.init.ast_rep[0];
	}

	public void visit(FuncDec x) throws Exception
	{
		// construct sub-nodes and count length
		int lc = 1;
		ListIterator<VarDec> vlit = x.var.listIterator();
		ListIterator<Stmt> slit = x.st.listIterator();
		while (vlit.hasNext())
		{
			VarDec cvd = vlit.next();
			cvd.accept(this);
			lc += cvd.ast_rep.length;
		}
		while (slit.hasNext())
		{
			Stmt cst = slit.next();
			cst.accept(this);
			lc += cst.ast_rep.length;
		}

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "Func: ".intern() + x.name + " (".intern();
		if (x.param.isEmpty())
			x.ast_rep[0] += Token.raw_rep(Token.VOID);
		else
		{
			ListIterator<Parameter> plit = x.param.listIterator();
			x.ast_rep[0] += plit.next().type.toString();
			while (plit.hasNext())
				x.ast_rep[0] += ", ".intern() + plit.next().type.toString();
		}

		x.ast_rep[0] += ") -> ".intern() + x.ret_type.toString();

		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add nodes' content
		int cl = 1;
		vlit = x.var.listIterator();
		slit = x.st.listIterator();
		while (vlit.hasNext())
		{
			VarDec cvd = vlit.next();
			for (String str : cvd.ast_rep)
				x.ast_rep[cl++] += str;
		}
		while (slit.hasNext())
		{
			Stmt cst = slit.next();
			for (String str : cst.ast_rep)
				x.ast_rep[cl++] += str;
		}
	}

	/* Init */
	@Override
	public void visit(Init x) throws Exception
	{
		if (x.listed)
		{
			// count lines and construct components
			int lc = 1;
			ListIterator<Init> lit = x.init_list.listIterator();
			while (lit.hasNext())
			{
				Init ci = lit.next();
				ci.accept(this);
				lc += ci.ast_rep.length;
			}

			// initialize format
			x.ast_rep = new String[lc];
			x.ast_rep[0] = leading + "InitializerList";
			for (int i = 1; i < lc; i++)
				x.ast_rep[i] = separator;

			// add contents
			int cl = 1;
			lit = x.init_list.listIterator();
			while (lit.hasNext())
			{
				Init ci = lit.next();
				for (String str : ci.ast_rep)
					x.ast_rep[cl++] += str;
			}
		}
		else
		{
			// construct components and count lines
			int lc = 1;
			x.exp.accept(this);
			lc += x.exp.ast_rep.length;

			// initialize format
			x.ast_rep = new String[lc];
			x.ast_rep[0] = leading + "Initializer";
			for (int i = 1; i < lc; i++)
				x.ast_rep[i] = separator;

			// add contents
			int cl = 1;
			for (String str : x.exp.ast_rep)
				x.ast_rep[cl++] += str;
		}
	}

	/* Stmt */
	@Override
	public void visit(ExprStmt x) throws Exception
	{
		// construct contents and count lines
		int lc = 1;
		if (x.exp != null)
		{
			x.exp.accept(this);
			lc += x.exp.ast_rep.length;
		}

		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "ExpressionStmt";
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		if (x.exp != null)
		{
			int cl = 1;
			for (String str : x.exp.ast_rep)
				x.ast_rep[cl++] += str;
		}
		else
			x.ast_rep[0] += ": empty";
	}

	@Override
	public void visit(CompStmt x) throws Exception
	{
		// construct components and count lines
		int lc = 1;
		ListIterator<VarDec> vlit = x.var.listIterator();
		while (vlit.hasNext())
		{
			VarDec cvd = vlit.next();
			cvd.accept(this);
			lc += cvd.ast_rep.length;
		}

		ListIterator<Stmt> slit = x.st.listIterator();
		while (slit.hasNext())
		{
			Stmt cst = slit.next();
			cst.accept(this);
			lc += cst.ast_rep.length;
		}

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "CompoundStmt";
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;

		vlit = x.var.listIterator();
		while (vlit.hasNext())
		{
			VarDec cvd = vlit.next();
			for (String str : cvd.ast_rep)
				x.ast_rep[cl++] += str;
		}

		slit = x.st.listIterator();
		while (slit.hasNext())
		{
			Stmt cst = slit.next();
			for (String str : cst.ast_rep)
				x.ast_rep[cl++] += str;
		}
	}

	@Override
	public void visit(SelectStmt x) throws Exception
	{
		// construct sub-nodes and count lines
		int lc = 1;

		x.condition.accept(this);
		lc += x.condition.ast_rep.length;

		x.if_branch.accept(this);
		lc += x.if_branch.ast_rep.length;

		if (x.else_branch != null)
		{
			x.else_branch.accept(this);
			lc += x.else_branch.ast_rep.length;
		}

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "SelectionStmt";
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;

		for (String str : x.condition.ast_rep)
			x.ast_rep[cl++] += str;

		for (String str : x.if_branch.ast_rep)
			x.ast_rep[cl++] += str;

		if (x.else_branch != null)
			for (String str : x.else_branch.ast_rep)
				x.ast_rep[cl++] += str;
	}

	@Override
	public void visit(IterStmt x) throws Exception
	{
		// construct sub-nodes and count lines
		int lc = 1;

		if (x.init != null)
		{
			x.init.accept(this);
			lc += x.init.ast_rep.length;
		}

		if (x.judge != null)
		{
			x.judge.accept(this);
			lc += x.judge.ast_rep.length;
		}

		if (x.next != null)
		{
			x.next.accept(this);
			lc += x.next.ast_rep.length;
		}

		x.stmt.accept(this);
		lc += x.stmt.ast_rep.length;

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading;
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		switch (x.category)
		{
		case IterStmt.iter_for:
			x.ast_rep[0] += "FOR_";
			x.ast_rep[0] += x.init != null ? 'O' : 'X';
			x.ast_rep[0] += x.judge != null ? 'O' : 'X';
			x.ast_rep[0] += x.next != null ? 'O' : 'X';
			break;
		case IterStmt.iter_while:
			x.ast_rep[0] += "WHILE";
			break;
		default:
			break;
		}

		int cl = 1;
		if (x.category == IterStmt.iter_while)
		{
			for (String str : x.judge.ast_rep)
				x.ast_rep[cl++] += str;

			for (String str : x.stmt.ast_rep)
				x.ast_rep[cl++] += str;
		}
		else if (x.category == IterStmt.iter_for)
		{
			if (x.init != null)
				for (String str : x.init.ast_rep)
					x.ast_rep[cl++] += str;

			if (x.judge != null)
				for (String str : x.judge.ast_rep)
					x.ast_rep[cl++] += str;

			if (x.next != null)
				for (String str : x.next.ast_rep)
					x.ast_rep[cl++] += str;

			for (String str : x.stmt.ast_rep)
				x.ast_rep[cl++] += str;
		}
		else
			return;
	}

	@Override
	public void visit(JumpStmt js) throws Exception
	{
		// construct sub-nodes and count lines
		int lc = 1;
		if (js.exp != null)
		{
			js.exp.accept(this);
			lc += js.exp.ast_rep.length;
		}

		js.ast_rep = new String[lc];

		// initialize format
		js.ast_rep[0] = leading;
		for (int i = 1; i < lc; i++)
			js.ast_rep[i] = separator;

		// add components
		switch (js.category)
		{
		case JumpStmt.jp_ctn:
			js.ast_rep[0] += "CONTINUE";
			break;
		case JumpStmt.jp_brk:
			js.ast_rep[0] += "BREAK";
			break;
		case JumpStmt.jp_ret:
			js.ast_rep[0] += "RETURN";
			break;
		default:
			break;
		}

		if (js.exp != null)
		{
			int cl = 1;
			for (String str : js.exp.ast_rep)
				js.ast_rep[cl++] += str;
		}
	}

	/* Exp */
	public void visit(CommaExp x) throws Exception
	{
		// construct nodes and count lines
		int lc = 1;
		ListIterator<Exp> lit = x.exp.listIterator();
		while (lit.hasNext())
		{
			Exp ae = lit.next();
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
			Exp ae = lit.next();
			for (String str : ae.ast_rep)
				x.ast_rep[cl++] += str;
		}
	}

	@Override
	public void visit(AssignExp x) throws Exception
	{
		// construct nodes and count lines
		int lc = 1;
		x.left.accept(this);
		lc += x.left.ast_rep.length;
		x.right.accept(this);
		lc += x.right.ast_rep.length;

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "AssignExp: ".intern() + x.get_op();
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add sub-nodes' content
		int cl = 1;
		for (String str : x.left.ast_rep)
			x.ast_rep[cl++] += str;
		for (String str : x.right.ast_rep)
			x.ast_rep[cl++] += str;
	}

	@Override
	public void visit(BinaryExp x) throws Exception
	{
		// construct sub-nodes and count lines
		int lc = 1;
		x.left.accept(this);
		lc += x.left.ast_rep.length;
		x.right.accept(this);
		lc += x.right.ast_rep.length;

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "BinaryExp: ".intern() + x.get_op();
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add sub-nodes' content
		int cl = 1;
		for (String str : x.left.ast_rep)
			x.ast_rep[cl++] += str;
		for (String str : x.right.ast_rep)
			x.ast_rep[cl++] += str;
	}

	public void visit(CastExp x) throws Exception
	{
		// construct sub-nodes
		// and count lines
		int lc = 2;
		x.exp.accept(this);
		lc += x.exp.ast_rep.length;

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "CastExp: ".intern();

		ListIterator<Type> lit = x.tp_seq.listIterator();
		while (lit.hasNext())
			x.ast_rep[0] += " -> ".intern() + lit.next().toString();

		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add sub-nodes' content
		int cl = 1;
		for (String str : x.exp.ast_rep)
			x.ast_rep[cl++] += str;
	}

	public void visit(UnaryExp x) throws Exception
	{
		// construct sub-nodes
		// count lines
		int lc = 2;
		if (x.exp != null)
		{
			x.exp.accept(this);
			lc += x.exp.ast_rep.length;
		}
		else
			lc += 1;

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "UnaryExp".intern();
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add sub-nodes' content
		switch (x.category)
		{
		case UnaryExp.address_of:
			x.ast_rep[1] += leading + "Address of ";
			break;
		case UnaryExp.indirection:
			x.ast_rep[1] += leading + "Indirection ";
			break;
		case UnaryExp.unary_plus:
			x.ast_rep[1] += leading + "Unary plus ";
			break;
		case UnaryExp.unary_minus:
			x.ast_rep[1] += leading + "Unary minus ";
			break;
		case UnaryExp.bitwise_not:
			x.ast_rep[1] += leading + "Bitwise not ";
			break;
		case UnaryExp.logical_negation:
			x.ast_rep[1] += leading + "Logical negation ";
			break;
		case UnaryExp.inc:
			x.ast_rep[1] += leading + "Prefix inc ";
			break;
		case UnaryExp.dec:
			x.ast_rep[1] += leading + "Prefix dec ";
			break;
		case UnaryExp.size_of:
			x.ast_rep[1] += leading + "Size_of ";
			break;
		default:
			break;
		}

		int cl = 2;
		if (x.exp != null)
			for (String str : x.exp.ast_rep)
				x.ast_rep[cl++] += str;
		else
			x.ast_rep[cl++] += x.stp.toString();
	}

	public void visit(PostfixExp x) throws Exception
	{
		// construct and count
		int lc = 1;
		x.pe.accept(this);
		lc += x.pe.ast_rep.length;

		ListIterator<PostfixElem> lit = x.elem.listIterator();
		while (lit.hasNext())
		{
			PostfixElem pfx = lit.next();
			switch (pfx.category)
			{
			case PostfixExp.PostfixElem.post_idx:
				pfx.exp.accept(this);
				lc += pfx.exp.ast_rep.length + 1;
				break;
			case PostfixExp.PostfixElem.post_call:
				lc += 1;
				if (pfx.exp != null)
				{
					pfx.exp.accept(this);
					lc += pfx.exp.ast_rep.length;
				}
				break;
			case PostfixExp.PostfixElem.post_arrow:
			case PostfixExp.PostfixElem.post_dot:
				lc += 2;
				break;
			case PostfixExp.PostfixElem.post_inc:
			case PostfixExp.PostfixElem.post_dec:
				lc += 1;
				break;
			default:
				break;
			}
		}

		// initialize format
		x.ast_rep = new String[lc];
		x.ast_rep[0] = leading + "PostfixExp".intern();
		for (int i = 1; i < lc; i++)
			x.ast_rep[i] = separator;

		// add contents
		int cl = 1;
		for (String str : x.pe.ast_rep)
			x.ast_rep[cl++] += str;

		lit = x.elem.listIterator();
		while (lit.hasNext())
		{
			PostfixElem pfx = lit.next();
			switch (pfx.category)
			{
			case PostfixExp.PostfixElem.post_idx:
				x.ast_rep[cl++] += "Index";
				for (String str : pfx.exp.ast_rep)
					x.ast_rep[cl++] += str;
				break;
			case PostfixExp.PostfixElem.post_call:
				x.ast_rep[cl++] += pfx.exp == null ? "Call" : "Call with parameters";
				if (pfx.exp != null)
					for (String str : pfx.exp.ast_rep)
						x.ast_rep[cl++] += str;
				break;
			case PostfixExp.PostfixElem.post_arrow:
				x.ast_rep[cl++] += "Structure dereference";
				x.ast_rep[cl++] += pfx.id;
				break;
			case PostfixExp.PostfixElem.post_dot:
				x.ast_rep[cl++] += "Structure reference";
				x.ast_rep[cl++] += pfx.id;
				break;
			case PostfixExp.PostfixElem.post_inc:
				x.ast_rep[cl++] += "Post increment";
				break;
			case PostfixExp.PostfixElem.post_dec:
				x.ast_rep[cl++] += "Post decrement";
				break;
			default:
				break;
			}
		}
	}

	public void visit(PrimaryExp x) throws Exception
	{
		switch (x.category)
		{
		case PrimaryExp.pe_id:

			x.ast_rep = new String[1];
			x.ast_rep[0] = leading + "Id: ".intern() + ((Dec) x.value).name;
			break;
		case PrimaryExp.pe_str:
			x.ast_rep = new String[1];
			x.ast_rep[0] = leading + "Str: \'".intern() + (String) x.value + '\'';
			break;
		case PrimaryExp.pe_int:
			x.ast_rep = new String[1];
			x.ast_rep[0] = leading + "Int-Const: " + ((Integer) x.value).toString();
			break;
		case PrimaryExp.pe_ch:
			x.ast_rep = new String[1];
			x.ast_rep[0] = leading + "Char-Const: \'".intern() + ((Character) x.value).toString() + '\'';
			break;
		case PrimaryExp.pe_fp:
			x.ast_rep = new String[1];
			x.ast_rep[0] = leading + "FP-Const: ".intern() + ((Float) x.value).toString() + '\'';
			break;
		case PrimaryExp.pe_paren:
			Exp e = (Exp) x.value;
			e.accept(this);
			x.ast_rep = new String[e.ast_rep.length];
			int cl = 0;
			for (String str : e.ast_rep)
				x.ast_rep[cl++] = str;
			break;
		default:
			break;
		}
	}
}
