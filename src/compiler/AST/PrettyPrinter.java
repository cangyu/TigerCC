package compiler.AST;

import java.util.*;

import compiler.AST.PostfixExp.PostfixElem;
import compiler.Lexer.Token;
import compiler.Parser.*;
import compiler.Typing.Type;

public class PrettyPrinter implements ASTNodeVisitor
{
	// for simplicity, if an record appears in TypeName
	// or sizeof or ParameterList or any other inconvenient
	// places, just convert it into one line

	private static final String tab = "\t".intern();

	@Override
	public void visit(Prog x) throws Exception
	{
		int lc = 0;
		ListIterator<Dec> dlit = x.general_decl.listIterator();
		while (dlit.hasNext())
		{
			Dec cd = dlit.next();
			cd.accept(this);
			lc += (cd.code_rep.length + 1);
		}

		x.code_rep = new String[lc];
		str_init(x.code_rep, lc);

		int cl = 0;
		dlit = x.general_decl.listIterator();
		while (dlit.hasNext())
		{
			for (String str : dlit.next().code_rep)
				x.code_rep[cl++] += str;
			cl++;
		}
	}

	@Override
	public void visit(VarDec x) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FuncDec x) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(StmtList x) throws Exception
	{
		int lc = 0;
		StmtList y = x;
		while (y != null)
		{
			y.head.accept(this);
			lc += y.head.code_rep.length;
			y = y.next;
		}

		x.code_rep = new String[lc];
		str_init(x.code_rep, lc);

		int cl = 0;
		y = x;
		while (y != null)
		{
			for (String str : y.head.code_rep)
				x.code_rep[cl++] += str;
			y = y.next;
		}
	}

	@Override
	public void visit(Declaration x) throws Exception
	{
		x.ts.accept(this);
		if (x.init_declarator_list != null)
			x.init_declarator_list.accept(this);

		int lc = x.ts.code_rep.length, cl = 0;

		x.code_rep = new String[lc];
		str_init(x.code_rep, lc);

		for (String str : x.ts.code_rep)
			x.code_rep[cl++] += str;

		--cl;
		if (x.init_declarator_list != null)
			x.code_rep[cl] += (" " + x.init_declarator_list.code_rep[0]);
		x.code_rep[cl] += ";";
	}

	@Override
	public void visit(Declarator x) throws Exception
	{
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		x.plain_declarator.accept(this);
		x.code_rep[0] += x.plain_declarator.code_rep[0];
		Iterator<Exp> it = x.dimension.iterator();
		while (it.hasNext())
		{
			Exp ce = it.next();
			ce.accept(this);
			x.code_rep[0] += "[";
			x.code_rep[0] += ce.code_rep[0];
			x.code_rep[0] += "]";
		}
	}

	@Override
	public void visit(DeclarationList x) throws Exception
	{
		int lc = 0;
		DeclarationList y = x;
		while (y != null)
		{
			y.head.accept(this);
			lc += y.head.code_rep.length;
			y = y.next;
		}

		x.code_rep = new String[lc];
		str_init(x.code_rep, lc);

		int cl = 0;
		y = x;
		while (y != null)
		{
			for (String str : y.head.code_rep)
				x.code_rep[cl++] += str;
			y = y.next;
		}
	}

	@Override
	public void visit(DeclaratorList x) throws Exception
	{
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		DeclaratorList y = x;
		while (y != null)
		{
			y.head.accept(this);
			y = y.next;
		}

		y = x;
		x.code_rep[0] += y.head.code_rep[0];
		y = y.next;
		while (y != null)
		{
			x.code_rep[0] += ", ";
			x.code_rep[0] += y.head.code_rep[0];
			y = y.next;
		}
	}

	@Override
	public void visit(InitDeclarator x) throws Exception
	{
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		x.declarator.accept(this);
		x.code_rep[0] += x.declarator.code_rep[0];

		if (x.initializer != null)
		{
			x.initializer.accept(this);
			x.code_rep[0] += " = ";
			x.code_rep[0] += x.initializer.code_rep[0];
		}
	}

	@Override
	public void visit(InitDeclarators x) throws Exception
	{
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		InitDeclarators y = x;
		while (y != null)
		{
			y.head.accept(this);
			y = y.next;
		}

		y = x;
		x.code_rep[0] += y.head.code_rep[0];
		y = y.next;

		while (y != null)
		{
			x.code_rep[0] += ", ";
			x.code_rep[0] += y.head.code_rep[0];
			y = y.next;
		}
	}

	@Override
	public void visit(NonInitDeclaration x) throws Exception
	{
		x.type_specifier.accept(this);
		x.declarator_list.accept(this);

		int lc = x.type_specifier.code_rep.length;
		x.code_rep = new String[lc];
		str_init(x.code_rep, lc);

		int cl = 0;
		for (String str : x.type_specifier.code_rep)
			x.code_rep[cl++] += str;

		--cl;
		x.code_rep[cl] += (" " + x.declarator_list.code_rep[0] + ";");
	}

	@Override
	public void visit(NonInitDeclarationList x) throws Exception
	{
		int lc = 0;
		NonInitDeclarationList y = x;
		while (y != null)
		{
			y.head.accept(this);
			lc += y.head.code_rep.length;
			y = y.next;
		}

		x.code_rep = new String[lc];
		str_init(x.code_rep, lc);
		int cl = 0;
		y = x;
		while (y != null)
		{
			for (String str : y.head.code_rep)
				x.code_rep[cl++] += str;

			y = y.next;
		}
	}

	@Override
	public void visit(PlainDeclaration x) throws Exception
	{
		// put all in one line for simplicity
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		plain_visit(x.ts);
		x.dlr.accept(this);

		x.code_rep[0] += x.ts.code_rep[0];
		x.code_rep[0] += " ";
		x.code_rep[0] += x.dlr.code_rep[0];
	}

	private void plain_visit(TypeSpecifier x) throws Exception
	{
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		switch (x.type_detail)
		{
		case VOID:
			x.code_rep = new String[1];
			x.code_rep[0] = "void";
			return;
		case INT:
			x.code_rep = new String[1];
			x.code_rep[0] = "int";
			return;
		case CHAR:
			x.code_rep = new String[1];
			x.code_rep[0] = "char";
			return;
		default:
			break;
		}

		if (x.comp == null)
		{
			x.code_rep[0] += (x.type_detail == compiler.Parser.Type.STRUCT ? "struct" : "union");
			x.code_rep[0] += (" " + x.tag);
		}
		else
		{
			plain_visit(x.comp);

			x.code_rep[0] = (x.type_detail == compiler.Parser.Type.STRUCT ? "struct" : "union");
			if (x.tag != null)
				x.code_rep[0] += (" " + x.tag);

			x.code_rep[0] += "{ ";
			x.code_rep[0] += x.comp.code_rep[0];
			x.code_rep[0] += "}";
		}
	}

	private void plain_visit(NonInitDeclarationList x) throws Exception
	{
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		NonInitDeclarationList y = x;
		while (y != null)
		{
			plain_visit(y.head);
			y = y.next;
		}

		y = x;
		x.code_rep[0] += y.head.code_rep[0];
		y = y.next;

		if (y == null)
			x.code_rep[0] += " ";

		while (y != null)
		{
			x.code_rep[0] += " ";
			x.code_rep[0] += y.head.code_rep[0];
			y = y.next;
		}
	}

	private void plain_visit(NonInitDeclaration x) throws Exception
	{
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		plain_visit(x.type_specifier);
		x.declarator_list.accept(this);

		x.code_rep[0] += x.type_specifier.code_rep[0];
		x.code_rep[0] += " ";
		x.code_rep[0] += x.declarator_list.code_rep[0];
		x.code_rep[0] += ";";
	}

	@Override
	public void visit(PlainDeclarator x) throws Exception
	{
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		x.star_list.accept(this);
		x.code_rep[0] += x.star_list.code_rep[0];
		x.code_rep[0] += x.name;
	}

	@Override
	public void visit(FuncDef x) throws Exception
	{
		int lc = 0;

		x.ts.accept(this);
		lc += x.ts.code_rep.length;

		x.pd.accept(this);

		if (x.pm != null)
			x.pm.accept(this);

		x.cst.accept(this);
		lc += x.cst.code_rep.length;

		x.code_rep = new String[lc];
		str_init(x.code_rep, lc);

		int cl = 0;
		for (String str : x.ts.code_rep)
			x.code_rep[cl++] += str;

		--cl;
		x.code_rep[cl] += (" " + x.pd.code_rep[0]);
		if (x.pm == null)
			x.code_rep[cl++] += "()";
		else
		{
			x.code_rep[cl] += "(";
			x.code_rep[cl] += x.pm.code_rep[0];
			x.code_rep[cl++] += ")";
		}

		for (String str : x.cst.code_rep)
			x.code_rep[cl++] += str;
	}

	@Override
	public void visit(Arguments x) throws Exception
	{
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		Arguments y = x;
		while (y != null)
		{
			y.head.accept(this);
			y = y.next;
		}

		y = x;
		x.code_rep[0] += y.head.code_rep[0];
		y = y.next;
		while (y != null)
		{
			x.code_rep[0] += (", " + y.head.code_rep[0]);
			y = y.next;
		}
	}

	@Override
	public void visit(ParameterList x) throws Exception
	{
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		ParameterList y = x;
		while (y != null)
		{
			y.head.accept(this);
			y = y.next;
		}

		y = x;
		x.code_rep[0] += y.head.code_rep[0];
		y = y.next;
		while (y != null)
		{
			x.code_rep[0] += (", " + y.head.code_rep[0]);
			y = y.next;
		}
	}

	@Override
	public void visit(TypeName x) throws Exception
	{
		// put all in one line for simplicity
		x.code_rep = new String[1];
		str_init(x.code_rep, 1);

		plain_visit(x.type_specifier);
		x.star_list.accept(this);

		x.code_rep[0] += x.type_specifier.code_rep[0];

		if (x.star_list.cnt > 0)
			x.code_rep[0] += (" " + x.star_list.code_rep[0]);
	}

	private static void str_init(String[] s, int num)
	{
		for (int i = 0; i < num; i++)
			s[i] = "".intern();
	}

	/* Init */
	@Override
	public void visit(Init x) throws Exception
	{
		// put all in one line for simplicity
		x.code_rep = new String[1];

		if (x.listed)
		{
			ListIterator<Init> lit = x.init_list.listIterator();
			while (lit.hasNext())
				lit.next().accept(this);

			x.code_rep[0] = Token.raw_rep(Token.LBRACE);

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
			x.code_rep[0] = x.exp.code_rep[0];
		}
	}

	/* Stmt */
	@Override
	public void visit(ExprStmt x) throws Exception
	{
		x.code_rep = new String[1];
		if (x.exp != null)
		{
			x.exp.accept(this);
			x.code_rep[0] = x.exp.code_rep[0] + Token.raw_rep(Token.SEMI);
		}
		else
			x.code_rep[0] = Token.raw_rep(Token.SEMI);
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

		int cl = 0;
		x.code_rep[cl++] = Token.raw_rep(Token.LBRACE);

		vlit = x.var.listIterator();
		while (vlit.hasNext())
		{
			VarDec cvd = vlit.next();
			for (String str : cvd.code_rep)
				x.code_rep[cl++] = tab + str;
		}

		slit = x.st.listIterator();
		while (slit.hasNext())
		{
			Stmt cst = slit.next();
			for (String str : cst.code_rep)
				x.code_rep[cl++] = tab + str;
		}

		x.code_rep[cl] = Token.raw_rep(Token.RBRACE);
	}

	@Override
	public void visit(SelectStmt x) throws Exception
	{
		int lc = 1;
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
		x.code_rep[0] = Token.raw_rep(Token.IF) + Token.raw_rep(Token.LPAREN) + x.condition.code_rep[0] + Token.raw_rep(Token.RPAREN);

		int cl = 1;
		for (String str : x.if_branch.code_rep)
			x.code_rep[cl++] = str;

		if (x.else_branch != null)
		{
			x.code_rep[cl++] = Token.raw_rep(Token.ELSE);
			for (String str : x.else_branch.code_rep)
				x.code_rep[cl++] = str;
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

			x.code_rep[0] = Token.raw_rep(Token.FOR) + Token.raw_rep(Token.LPAREN);

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
				x.code_rep[cl++] += str;
		}
		else if (x.category == IterStmt.iter_while)
		{
			x.judge.accept(this);
			x.stmt.accept(this);
			lc = 1 + x.stmt.code_rep.length;
			x.code_rep = new String[lc];
			x.code_rep[0] = Token.raw_rep(Token.WHILE) + Token.raw_rep(Token.LPAREN) + x.judge.code_rep[0] + Token.raw_rep(Token.RPAREN);

			int cl = 1;
			for (String str : x.stmt.code_rep)
				x.code_rep[cl++] += str;
		}
		else
			return;
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
			if (cnt != 0)
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
		x.code_rep[0] += x.left.code_rep[0] + " " + x.get_op() + " " + x.right.code_rep[0];
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
