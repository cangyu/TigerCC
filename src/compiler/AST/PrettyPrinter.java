package compiler.AST;

import java.util.*;
import compiler.AST.PostfixExp.PostfixElem;
import compiler.Lexer.Token;
import compiler.Typing.*;

public class PrettyPrinter implements ASTNodeVisitor
{
	// for simplicity, if an record appears in TypeName
	// or sizeof or ParameterList or any other inconvenient
	// places, just convert it into one line

	private Prog entrance;

	public PrettyPrinter(Prog x)
	{
		entrance = x;
	}

	public void print() throws Exception
	{
		visit(entrance);
		for (String str : entrance.code_rep)
			System.out.println(str);
	}

	private static final String tab = "\t".intern();

	private static void str_init(String[] s, int num)
	{
		for (int i = 0; i < num; i++)
			s[i] = "".intern();
	}

	private static String c_form(Type t, String id)
	{
		String ret = "";
		Type ct = t;

		// possible array dimensions
		while (ct instanceof Array)
		{
			Array ca = (Array) ct;
			ret += String.format("[%d]", ca.elem_num);
			ct = ca.elem_type;
		}

		// possible ptrs
		String ptr = "";
		while (ct instanceof Pointer)
		{
			Pointer cpt = (Pointer) ct;
			ptr += "*".intern();
			ct = cpt.elem_type;
		}

		// basic type
		// Here we simplify the record representation
		ret = ct.toString() + ptr + " " + id + ret;

		return ret;
	}

	/* Prog */
	@Override
	public void visit(Prog x) throws Exception
	{
		int lc = 0;
		ListIterator<Dec> dlit = x.general_decl.listIterator();
		while (dlit.hasNext())
		{
			Dec cd = dlit.next();
			cd.accept(this);
			lc += cd.code_rep.length;
		}

		x.code_rep = new String[lc];
		str_init(x.code_rep, lc);

		int cl = 0;
		dlit = x.general_decl.listIterator();
		while (dlit.hasNext())
		{
			Dec cd = dlit.next();
			for (String str : cd.code_rep)
				x.code_rep[cl++] = str;
		}
	}

	@Override
	public void visit(VarDec x) throws Exception
	{
		x.code_rep = new String[1];
		x.code_rep[0] = c_form(x.type, x.name);
		if (x.init != null)
		{
			x.init.accept(this);
			x.code_rep[0] += " = " + x.init.code_rep[0];
		}
		x.code_rep[0] += ";".intern();
	}

	@Override
	public void visit(FuncDec x) throws Exception
	{
		// construct contents and count lines
		int lc = 3;

		ListIterator<VarDec> vlit = x.var.listIterator();
		int arg_num = x.param.size(), cnt = 0;
		while (vlit.hasNext())
		{
			VarDec cvd = vlit.next();
			if (cnt++ < arg_num)
				continue;

			cvd.accept(this);
			lc += cvd.code_rep.length;
		}

		ListIterator<Stmt> slit = x.st.listIterator();
		while (slit.hasNext())
		{
			Stmt cst = slit.next();
			cst.accept(this);
			lc += cst.code_rep.length;
		}

		// initialize format
		x.code_rep = new String[lc];
		str_init(x.code_rep, lc);

		// add func-name and parameters
		x.code_rep[0] = c_form(x.ret_type, x.name) + Token.raw_rep(Token.LPAREN);
		ListIterator<FuncDec.Parameter> plit = x.param.listIterator();
		cnt = 0;
		while (plit.hasNext())
		{
			if (cnt++ != 0)
				x.code_rep[0] += Token.raw_rep(Token.COMMA) + " ";

			FuncDec.Parameter cpm = plit.next();
			x.code_rep[0] += c_form(cpm.type, cpm.name);
		}
		x.code_rep[0] += Token.raw_rep(Token.RPAREN);
		x.code_rep[1] += Token.raw_rep(Token.LBRACE);

		// body
		int cl = 2;
		vlit = x.var.listIterator();
		cnt = 0;
		while (vlit.hasNext())
		{
			VarDec cvd = vlit.next();
			if (cnt++ < arg_num)
				continue;

			for (String str : cvd.code_rep)
				x.code_rep[cl++] += tab + str;
		}

		slit = x.st.listIterator();
		while (slit.hasNext())
		{
			Stmt cst = slit.next();
			for (String str : cst.code_rep)
				x.code_rep[cl++] += tab + str;
		}

		x.code_rep[cl] += Token.raw_rep(Token.RBRACE);
	}

	/* Init */
	@Override
	public void visit(Init x) throws Exception
	{
		// put all in one line for simplicity
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		if (x.listed)
		{
			ListIterator<Init> lit = x.init_list.listIterator();
			while (lit.hasNext())
				lit.next().accept(this);

			x.code_rep[0] += Token.raw_rep(Token.LBRACE);

			int cnt = 0;
			lit = x.init_list.listIterator();
			while (lit.hasNext())
			{
				if (cnt++ != 0)
					x.code_rep[0] += Token.raw_rep(Token.COMMA) + " ";

				x.code_rep[0] += lit.next().code_rep[0];
			}

			x.code_rep[0] += Token.raw_rep(Token.RBRACE);
		}
		else
		{
			x.exp.accept(this);
			x.code_rep[0] += x.exp.code_rep[0];
		}
	}

	/* Stmt */
	@Override
	public void visit(ExprStmt x) throws Exception
	{
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		if (x.exp != null)
		{
			x.exp.accept(this);
			x.code_rep[0] += x.exp.code_rep[0];
		}

		x.code_rep[0] += Token.raw_rep(Token.SEMI);
	}

	@Override
	public void visit(CompStmt x) throws Exception
	{
		int lc = 2;
		ListIterator<VarDec> vlit = x.var.listIterator();
		while (vlit.hasNext())
		{
			VarDec cvd = vlit.next();
			cvd.accept(this);
			lc += cvd.code_rep.length;
		}

		ListIterator<Stmt> slit = x.st.listIterator();
		while (slit.hasNext())
		{
			Stmt cst = slit.next();
			cst.accept(this);
			lc += cst.code_rep.length;
		}

		x.code_rep = new String[lc];
		str_init(x.code_rep, lc);
		x.code_rep[0] += Token.raw_rep(Token.LBRACE);

		int cl = 1;
		vlit = x.var.listIterator();
		while (vlit.hasNext())
		{
			VarDec cvd = vlit.next();
			for (String str : cvd.code_rep)
				x.code_rep[cl++] += tab + str;
		}

		slit = x.st.listIterator();
		while (slit.hasNext())
		{
			Stmt cst = slit.next();
			for (String str : cst.code_rep)
				x.code_rep[cl++] += tab + str;
		}

		x.code_rep[cl] += Token.raw_rep(Token.RBRACE);
	}

	@Override
	public void visit(SelectStmt x) throws Exception
	{
		int lc = 0;
		x.condition.accept(this);
		lc += x.condition.code_rep.length;

		x.if_branch.accept(this);
		lc += x.if_branch.code_rep.length;

		if (x.else_branch != null)
		{
			lc += 1;
			x.else_branch.accept(this);
			lc += x.else_branch.code_rep.length;
		}

		x.code_rep = new String[lc];
		str_init(x.code_rep, lc);

		x.code_rep[0] += Token.raw_rep(Token.IF) + Token.raw_rep(Token.LPAREN) + x.condition.code_rep[0] + Token.raw_rep(Token.RPAREN);

		int cl = 1;
		for (String str : x.if_branch.code_rep)
			x.code_rep[cl++] += x.if_branch instanceof CompStmt ? "" : tab + str;

		if (x.else_branch != null)
		{
			x.code_rep[cl++] = Token.raw_rep(Token.ELSE);
			for (String str : x.else_branch.code_rep)
				x.code_rep[cl++] = x.if_branch instanceof CompStmt ? "" : tab + str;
		}
	}

	@Override
	public void visit(IterStmt x) throws Exception
	{
		int lc = 1;
		if (x.category == IterStmt.iter_for)
		{
			if (x.init != null)
				x.init.accept(this);
			if (x.judge != null)
				x.judge.accept(this);
			if (x.next != null)
				x.next.accept(this);

			x.stmt.accept(this);
			lc += x.stmt.code_rep.length;

			x.code_rep = new String[lc];
			str_init(x.code_rep, lc);

			x.code_rep[0] += Token.raw_rep(Token.FOR) + Token.raw_rep(Token.LPAREN);

			if (x.init != null)
				x.code_rep[0] += x.init.code_rep[0];
			x.code_rep[0] += Token.raw_rep(Token.SEMI);

			if (x.judge != null)
				x.code_rep[0] += x.judge.code_rep[0];
			x.code_rep[0] += Token.raw_rep(Token.SEMI);

			if (x.next != null)
				x.code_rep[0] += x.next.code_rep[0];
			x.code_rep[0] += Token.raw_rep(Token.RPAREN);

			int cl = 1;
			for (String str : x.stmt.code_rep)
				x.code_rep[cl++] += x.stmt instanceof CompStmt ? "" : tab + str;
		}
		else
		{
			x.judge.accept(this);
			x.stmt.accept(this);
			lc = 1 + x.stmt.code_rep.length;
			x.code_rep = new String[lc];
			str_init(x.code_rep, lc);
			x.code_rep[0] += Token.raw_rep(Token.WHILE) + Token.raw_rep(Token.LPAREN) + x.judge.code_rep[0] + Token.raw_rep(Token.RPAREN);

			int cl = 1;
			for (String str : x.stmt.code_rep)
				x.code_rep[cl++] += x.stmt instanceof CompStmt ? "" : tab + str;
		}
	}

	@Override
	public void visit(JumpStmt x) throws Exception
	{
		x.code_rep = new String[1];
		if (x.exp != null)
			x.exp.accept(this);

		switch (x.category)
		{
		case JumpStmt.jp_ret:
			x.code_rep[0] = Token.raw_rep(Token.RETURN);
			if (x.exp != null)
				x.code_rep[0] += " " + x.exp.code_rep[0];
			break;
		case JumpStmt.jp_ctn:
			x.code_rep[0] = Token.raw_rep(Token.CONTINUE);
			break;
		case JumpStmt.jp_brk:
			x.code_rep[0] = Token.raw_rep(Token.BREAK);
			break;
		default:
			break;
		}
		x.code_rep[0] += Token.raw_rep(Token.SEMI);
	}

	/* Exp */
	@Override
	public void visit(CommaExp x) throws Exception
	{
		ListIterator<Exp> lit = x.exp.listIterator();
		while (lit.hasNext())
		{
			Exp te = lit.next();
			te.accept(this);
		}

		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		int cnt = 0;
		lit = x.exp.listIterator();
		while (lit.hasNext())
		{
			if (cnt++ != 0)
				x.code_rep[0] += ", ".intern();

			Exp te = lit.next();
			x.code_rep[0] += te.code_rep[0];
		}
	}

	@Override
	public void visit(AssignExp x) throws Exception
	{
		x.left.accept(this);
		x.right.accept(this);

		x.code_rep = new String[1];
		x.code_rep[0] = x.left.code_rep[0] + " " + x.get_op() + " " + x.right.code_rep[0];
	}

	@Override
	public void visit(BinaryExp x) throws Exception
	{
		x.left.accept(this);
		x.right.accept(this);

		x.code_rep = new String[1];
		x.code_rep[0] = x.left.code_rep[0] + " " + x.get_op() + " " + x.right.code_rep[0];
	}

	@Override
	public void visit(CastExp x) throws Exception
	{
		x.exp.accept(this);

		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		int tcn = x.tp_seq.size();
		ListIterator<Type> lit = x.tp_seq.listIterator(tcn);
		while (lit.hasPrevious())
		{
			Type ctp = lit.previous();
			x.code_rep[0] += "(" + ctp.toString() + ")";
		}

		x.code_rep[0] += x.exp.code_rep[0];
	}

	@Override
	public void visit(UnaryExp x) throws Exception
	{
		if (x.exp != null)
			x.exp.accept(this);

		x.code_rep = new String[1];
		x.code_rep[0] = x.get_op();

		if (x.stp != null)
			x.code_rep[0] += "(" + x.stp.toString() + ")";
		else
		{
			if (x.category == UnaryExp.size_of)
				x.code_rep[0] += " ";

			x.code_rep[0] += x.exp.code_rep[0];
		}
	}

	@Override
	public void visit(PostfixExp x) throws Exception
	{
		x.pe.accept(this);

		x.code_rep = new String[1];
		x.code_rep[0] = x.pe.code_rep[0];

		ListIterator<PostfixElem> lit = x.elem.listIterator();
		while (lit.hasNext())
		{
			PostfixElem pfx = lit.next();
			switch (pfx.category)
			{
			case PostfixExp.PostfixElem.post_idx:
				Exp e = (Exp) pfx.exp;
				e.accept(this);
				x.code_rep[0] += "[" + e.code_rep[0] + "]";
				break;
			case PostfixExp.PostfixElem.post_call:
				Exp args = (Exp) pfx.exp;
				args.accept(this);
				x.code_rep[0] += "(" + args.code_rep[0] + ")";
				break;
			case PostfixExp.PostfixElem.post_arrow:
				x.code_rep[0] += Token.raw_rep(Token.PTR) + pfx.id;
				break;
			case PostfixExp.PostfixElem.post_dot:
				x.code_rep[0] += Token.raw_rep(Token.DOT) + pfx.id;
				break;
			case PostfixExp.PostfixElem.post_inc:
				x.code_rep[0] += Token.raw_rep(Token.INC);
				break;
			case PostfixExp.PostfixElem.post_dec:
				x.code_rep[0] += Token.raw_rep(Token.DEC);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void visit(PrimaryExp x) throws Exception
	{
		x.code_rep = new String[1];

		switch (x.category)
		{
		case PrimaryExp.pe_id:
			x.code_rep[0] = ((Dec) x.value).name;
			break;
		case PrimaryExp.pe_str:
			x.code_rep[0] = (String) x.value;
			break;
		case PrimaryExp.pe_ch:
			x.code_rep[0] = ((Character) x.value).toString();
			break;
		case PrimaryExp.pe_int:
			x.code_rep[0] = ((Integer) x.value).toString();
			break;
		case PrimaryExp.pe_fp:
			x.code_rep[0] = ((Float) x.value).toString();
			break;
		case PrimaryExp.pe_paren:
			Exp e = (Exp) x.value;
			e.accept(this);
			x.code_rep[0] = "(" + e.code_rep[0] + ")";
			break;
		default:
			break;
		}
	}
}
