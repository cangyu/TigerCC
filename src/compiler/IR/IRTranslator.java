package compiler.IR;

import java.util.*;
import compiler.AST.*;
import compiler.AST.PostfixExp.PostfixElem;
import compiler.Frame.*;
import compiler.Scoping.*;
import compiler.Typing.*;

public class IRTranslator
{
	private Prog entrance;
	private IRCode code;
	private Stack<Label> begin_label, end_label;
	private Stack<Label> exit;
	private Frame global_frame;
	private Stack<Env> env;

	private static final Const izero = new Const(0);
	private static final Const ione = new Const(1);
	private static final Const iaf = new Const(0xFFFFFFFF);

	public IRTranslator(Prog x, Frame y)
	{
		entrance = x;
		global_frame = y;
		code = new IRCode();
		begin_label = new Stack<Label>();
		end_label = new Stack<Label>();
		exit = new Stack<Label>();
		env = new Stack<Env>();
	}

	public IRCode translate() throws Exception
	{
		env.push(entrance.venv);
		for (Dec dcl : entrance.general_decl)
		{
			if (dcl instanceof VarDec)
			{
				VarDec vd = (VarDec) dcl;
				transVarDec(vd);
			}
			else if (dcl instanceof FuncDec)
			{
				FuncDec fd = (FuncDec) dcl;
				exit.push(new Label());
				env.push(fd.scope);
				transFuncDec(fd);
				code.add_label(exit.peek());
				env.pop();
				exit.pop();
			}
			else
				internal_error();
		}
		env.pop();

		if (!env.isEmpty())
			internal_error();

		return code;
	}

	/* Dec */
	private void transVarDec(VarDec x) throws Exception
	{
		Init ci = x.init;
		if (ci != null)
		{
			Temp ival = transInit(ci);
			code.add_oper(new Move(ival, new Mem(x.offset)));
		}
	}

	private void transFuncDec(FuncDec x) throws Exception
	{
		// leading label
		Label lf = new Label(x.name);
		code.add_label(lf);

		// parameters and local variables
		ListIterator<VarDec> vlit = x.var.listIterator();
		while (vlit.hasNext())
			transLocalVar(vlit.next());

		// statements
		ListIterator<Stmt> slit = x.st.listIterator();
		while (slit.hasNext())
			transStmt(slit.next());
	}

	private void transLocalVar(VarDec x)
	{

	}

	/* Exp */
	private Temp transExp(Exp x) throws Exception
	{
		Temp ret = null;

		if (x.isConst && Type.arith(x.type))
		{
			if(x.value==null)
				internal_error();
			
			Const cx = new Const(x.value);
			ret = new Temp();
			code.add_oper(new Move(cx, ret));
		}
		else if (x instanceof CommaExp)
			ret = transCommaExp((CommaExp) x);
		else if (x instanceof AssignExp)
			ret = transAssignExp((AssignExp) x);
		else if (x instanceof BinaryExp)
			ret = transBinaryExp((BinaryExp) x);
		else if (x instanceof CastExp)
			ret = transCastExp((CastExp) x);
		else if (x instanceof UnaryExp)
			ret = transUnaryExp((UnaryExp) x);
		else if (x instanceof PostfixExp)
			ret = transPfxExp((PostfixExp) x);
		else if (x instanceof PrimaryExp)
			ret = transPrimaryExp((PrimaryExp) x);
		else
			internal_error();

		return ret;
	}

	private Temp transCommaExp(CommaExp x) throws Exception
	{
		ListIterator<Exp> alit = x.exp.listIterator();
		Temp ret = null;
		while (alit.hasNext())
			ret = transExp(alit.next());
		return ret;
	}

	private Temp transAssignExp(AssignExp x) throws Exception
	{
		Temp lhs = transExp(x.left);
		Temp rhs = transExp(x.right);

		if (x.assign_type == AssignExp.plain)
			code.add_oper(new Move(rhs, lhs));
		else if (x.assign_type == AssignExp.add)
		{
			Temp ans = new Temp();
			code.add_oper(new BinOp(Quad.add, lhs, rhs, ans));
			code.add_oper(new Move(ans, lhs));
		}
		else if (x.assign_type == AssignExp.sub)
		{
			Temp ans = new Temp();
			code.add_oper(new BinOp(Quad.sub, lhs, rhs, ans));
			code.add_oper(new Move(ans, lhs));
		}
		else if (x.assign_type == AssignExp.multi)
		{
			Temp ans = new Temp();
			code.add_oper(new BinOp(Quad.mult, lhs, rhs, ans));
			code.add_oper(new Move(ans, lhs));
		}
		else if (x.assign_type == AssignExp.divide)
		{
			Temp ans = new Temp();
			code.add_oper(new BinOp(Quad.div, lhs, rhs, ans));
			code.add_oper(new Move(ans, lhs));
		}
		else if (x.assign_type == AssignExp.module)
		{
			Temp ans = new Temp();
			code.add_oper(new BinOp(Quad.mod, lhs, rhs, ans));
			code.add_oper(new Move(ans, lhs));
		}
		else if (x.assign_type == AssignExp.bit_and)
		{
			Temp ans = new Temp();
			code.add_oper(new BinOp(Quad.and, lhs, rhs, ans));
			code.add_oper(new Move(ans, lhs));
		}
		else if (x.assign_type == AssignExp.bit_or)
		{
			Temp ans = new Temp();
			code.add_oper(new BinOp(Quad.or, lhs, rhs, ans));
			code.add_oper(new Move(ans, lhs));
		}
		else if (x.assign_type == AssignExp.bit_xor)
		{
			Temp ans = new Temp();
			code.add_oper(new BinOp(Quad.xor, lhs, rhs, ans));
			code.add_oper(new Move(ans, lhs));
		}
		else if (x.assign_type == AssignExp.left_shift)
		{
			Temp ans = new Temp();
			code.add_oper(new BinOp(Quad.lshift, lhs, rhs, ans));
			code.add_oper(new Move(ans, lhs));
		}
		else if (x.assign_type == AssignExp.right_shift)
		{
			Temp ans = new Temp();
			code.add_oper(new BinOp(Quad.rshift, lhs, rhs, ans));
			code.add_oper(new Move(ans, lhs));
		}
		else
			internal_error();

		// write back in memory
		code.add_oper(new Move(lhs, getAccess(x.left)));
		return lhs;
	}

	private Temp transBinaryExp(BinaryExp x) throws Exception
	{
		Temp lhs = transExp(x.left);
		Temp rhs = transExp(x.right);
		Temp ans = new Temp();

		if (x.op == BinaryExp.multiply)
			code.add_oper(new BinOp(Quad.mult, lhs, rhs, ans));
		else if (x.op == BinaryExp.division)
			code.add_oper(new BinOp(Quad.div, lhs, rhs, ans));
		else if (x.op == BinaryExp.module)
			code.add_oper(new BinOp(Quad.mod, lhs, rhs, ans));
		else if (x.op == BinaryExp.addition)
			code.add_oper(new BinOp(Quad.add, lhs, rhs, ans));
		else if (x.op == BinaryExp.substraction)
			code.add_oper(new BinOp(Quad.sub, lhs, rhs, ans));
		else if (x.op == BinaryExp.bitwise_and)
			code.add_oper(new BinOp(Quad.and, lhs, rhs, ans));
		else if (x.op == BinaryExp.bitwise_or)
			code.add_oper(new BinOp(Quad.or, lhs, rhs, ans));
		else if (x.op == BinaryExp.bitwise_xor)
			code.add_oper(new BinOp(Quad.xor, lhs, rhs, ans));
		else if (x.op == BinaryExp.equal)
			code.add_oper(new BinOp(Quad.cmp_EQ, lhs, rhs, ans));
		else if (x.op == BinaryExp.not_equal)
			code.add_oper(new BinOp(Quad.cmp_NE, lhs, rhs, ans));
		else if (x.op == BinaryExp.greater_equal)
			code.add_oper(new BinOp(Quad.cmp_GE, lhs, rhs, ans));
		else if (x.op == BinaryExp.greater_than)
			code.add_oper(new BinOp(Quad.cmp_GT, lhs, rhs, ans));
		else if (x.op == BinaryExp.less_equal)
			code.add_oper(new BinOp(Quad.cmp_LE, lhs, rhs, ans));
		else if (x.op == BinaryExp.less_than)
			code.add_oper(new BinOp(Quad.cmp_LT, lhs, rhs, ans));
		else if (x.op == BinaryExp.logical_and)
		{
			Label fail = new Label();
			Label end = new Label();
			code.add_oper(new Branch(null, lhs, fail));
			code.add_oper(new Branch(null, rhs, fail));
			code.add_oper(new Move(ione, ans));
			code.add_oper(new Jump(end));
			code.add_label(fail);
			code.add_oper(new Move(izero, ans));
			code.add_label(end);
		}
		else if (x.op == BinaryExp.logical_or)
		{
			Label ok = new Label();
			Label end = new Label();
			code.add_oper(new Branch(lhs, null, ok));
			code.add_oper(new Branch(rhs, null, ok));
			code.add_oper(new Move(izero, ans));
			code.add_oper(new Jump(end));
			code.add_label(ok);
			code.add_oper(new Move(ione, ans));
			code.add_label(end);
		}
		else if (x.op == BinaryExp.shift_left)
			code.add_oper(new BinOp(Quad.lshift, lhs, rhs, ans));
		else if (x.op == BinaryExp.shift_right)
			code.add_oper(new BinOp(Quad.rshift, lhs, rhs, ans));
		else
			internal_error();

		return ans;
	}

	private Temp transCastExp(CastExp x) throws Exception
	{
		return transExp(x.exp);
	}

	private Temp transUnaryExp(UnaryExp x) throws Exception
	{
		Temp ans = new Temp();
		if (x.category == UnaryExp.address_of)
		{
			transExp(x.exp);
			Operand addr = getAddr(x.exp);
			code.add_oper(new Move(addr, ans));
		}
		else if (x.category == UnaryExp.indirection)
		{
			Temp tmp = transExp(x.exp);
			code.add_oper(new Move(new Mem(tmp), ans));
		}
		else if (x.category == UnaryExp.unary_plus)
		{
			Temp tmp = transExp(x.exp);
			code.add_oper(new Move(tmp, ans));
		}
		else if (x.category == UnaryExp.unary_minus)
		{
			Temp r1 = transExp(x.exp);
			Temp r2 = new Temp();
			code.add_oper(new BinOp(Quad.sub, izero, r1, r2));
			code.add_oper(new Move(r2, ans));
		}
		else if (x.category == UnaryExp.bitwise_not)
		{
			Temp tmp = transExp(x.exp);
			code.add_oper(new BinOp(Quad.xor, tmp, iaf, ans));
		}
		else if (x.category == UnaryExp.logical_negation)
		{
			Label ok = new Label();
			Label end = new Label();
			Temp tmp = transExp(x.exp);
			code.add_oper(new Branch(tmp, null, ok));
			code.add_oper(new Move(ione, ans));
			code.add_oper(new Jump(end));
			code.add_label(ok);
			code.add_oper(new Move(izero, ans));
			code.add_label(end);
		}
		else if (x.category == UnaryExp.size_of)
		{
			int sz = x.exp != null ? x.exp.type.width : x.type.width;
			Const isz = new Const(sz);
			code.add_oper(new Move(isz, ans));
		}
		else if (x.category == UnaryExp.inc)
		{
			Temp tmp = transExp(x.exp);
			code.add_oper(new BinOp(Quad.add, tmp, ione, ans));
			code.add_oper(new Move(ans, getAccess(x.exp)));
		}
		else if (x.category == UnaryExp.dec)
		{
			Temp tmp = transExp(x.exp);
			code.add_oper(new BinOp(Quad.sub, tmp, ione, ans));
			code.add_oper(new Move(ans, getAccess(x.exp)));
		}
		else
			internal_error();

		return ans;
	}

	private Temp transPfxExp(PostfixExp x) throws Exception
	{
		Temp ans = new Temp();
		Temp base_val = transExp(x.pe);

		ListIterator<PostfixElem> lit = x.elem.listIterator();
		while (lit.hasNext())
		{
			PostfixElem pfx = lit.next();
			if (pfx.category == PostfixElem.post_idx)
			{
				if (x.pe.type instanceof Pointer)
				{
					Temp dim = transExp(pfx.exp);
					Pointer ptr = (Pointer) x.pe.type;
					Const ul = new Const(ptr.elem_type.width);
					Temp off = new Temp();
					code.add_oper(new BinOp(Quad.mult, dim, ul, off));
					Temp da = new Temp();
					code.add_oper(new BinOp(Quad.add, base_val, off, da));
					code.add_oper(new Move(da, ans));
				}
				else if (x.pe.type instanceof Array)
				{
					Temp dim = transExp(pfx.exp);
					Array ary = (Array) x.pe.type;
					Const ul = new Const(ary.elem_type.width);
					Temp off = new Temp();
					code.add_oper(new BinOp(Quad.mult, dim, ul, off));
					Temp da = new Temp();
					code.add_oper(new BinOp(Quad.add, base_val, off, da));
					code.add_oper(new Move(da, ans));
				}
				else
					internal_error();
			}
			else if (pfx.category == PostfixElem.post_call)
			{
				String fn = ((FuncDec) x.pe.value).name;
				Call fc = new Call(new Label(fn));
				handleParam(pfx.exp, fc);
				code.add_oper(fc);
				code.add_oper(new Move(global_frame.RA(), ans));
			}
			else if (pfx.category == PostfixElem.post_dot)
			{
				if (x.pe.type instanceof Struct)
				{
					int off = 0;
					Struct cst = (Struct) x.pe.type;
					for (Symbol sym : cst.field.keySet())
					{
						if (sym.name.equals(pfx.id))
							break;
						off += cst.field.get(sym).width;
					}
					Const io = new Const(off);
					code.add_oper(new BinOp(Quad.add, base_val, io, ans));
				}
				else if (x.pe.type instanceof Union)
				{
					code.add_oper(new Move(base_val, ans));
				}
				else
					internal_error();
			}
			else if (pfx.category == PostfixElem.post_arrow)
			{
				Type ctp = ((Pointer) x.type).elem_type;
				if (ctp instanceof Struct)
				{
					int off = 0;
					Struct cst = (Struct) x.pe.type;
					for (Symbol sym : cst.field.keySet())
					{
						if (sym.name.equals(pfx.id))
							break;
						off += cst.field.get(sym).width;
					}
					Const io = new Const(off);
					code.add_oper(new BinOp(Quad.add, base_val, io, ans));
				}
				else if (x.pe.type instanceof Union)
				{
					code.add_oper(new Move(base_val, ans));
				}
				else
					internal_error();
			}
			else if (pfx.category == PostfixElem.post_inc)
			{
				code.add_oper(new Move(base_val, ans));
				code.add_oper(new BinOp(Quad.add, base_val, ione, base_val));
			}
			else if (pfx.category == PostfixElem.post_dec)
			{
				code.add_oper(new Move(base_val, ans));
				code.add_oper(new BinOp(Quad.sub, base_val, ione, base_val));
			}
			else
				internal_error();
		}

		return ans;
	}

	private Temp transPrimaryExp(PrimaryExp x) throws Exception
	{
		Temp ans = new Temp();

		if (x.category == PrimaryExp.pe_id)
		{
			String cid = ((Dec) x.value).name;
			Symbol csym = Symbol.getSymbol(cid);
			int off = ((Dec) env.peek().get_global(csym).mirror).offset;
			code.add_oper(new Move(new Mem(off), ans));
		}
		else if (x.category == PrimaryExp.pe_ch)
		{
			Character cch = (Character) x.value;
			char val = cch.charValue();
			Const ic = new Const(val);
			code.add_oper(new Move(ic, ans));
		}
		else if (x.category == PrimaryExp.pe_int)
		{
			Integer cint = (Integer) x.value;
			int val = cint.intValue();
			Const ic = new Const(val);
			code.add_oper(new Move(ic, ans));
		}
		else if (x.category == PrimaryExp.pe_fp)
		{
			Float cfp = (Float) x.value;
			float val = cfp.floatValue();
			Const ic = new Const(val);
			code.add_oper(new Move(ic, ans));
		}
		else if (x.category == PrimaryExp.pe_str)
		{
			String cstr = (String) x.value;
			Label lbl = new Label(cstr);
			code.add_label(lbl);
		}
		else if (x.category == PrimaryExp.pe_paren)
		{
			Temp tmp = transExp(x.ce);
			code.add_oper(new Move(tmp, ans));
		}
		else
			internal_error();

		return ans;
	}

	/* Stmt */
	private void transStmt(Stmt x) throws Exception
	{
		if (x instanceof CompStmt)
		{
			CompStmt cs = (CompStmt) x;
			env.push(cs.scope);
			transCompStmt(cs);
			env.pop();
		}
		else if (x instanceof ExprStmt)
			transExprStmt((ExprStmt) x);
		else if (x instanceof IterStmt)
			transIterStmt((IterStmt) x);
		else if (x instanceof JumpStmt)
			transJumpStmt((JumpStmt) x);
		else if (x instanceof SelectStmt)
			transSelectStmt((SelectStmt) x);
		else
			internal_error();
	}

	private void transExprStmt(ExprStmt x) throws Exception
	{
		if (x.exp != null)
			transExp(x.exp);
	}

	private void transCompStmt(CompStmt x) throws Exception
	{
		ListIterator<VarDec> vlit = x.var.listIterator();
		while (vlit.hasNext())
		{
			VarDec cvd = vlit.next();
			transVarDec(cvd);
		}

		ListIterator<Stmt> tlit = x.st.listIterator();
		while (tlit.hasNext())
		{
			Stmt cst = tlit.next();
			transStmt(cst);
		}
	}

	private void transSelectStmt(SelectStmt x) throws Exception
	{
		Temp jge = transExp(x.condition);
		if (x.else_branch != null)
		{
			Label begin_else = new Label();
			Label end = new Label();
			code.add_oper(new Branch(null, jge, begin_else));
			transStmt(x.if_branch);
			code.add_oper(new Jump(end));
			code.add_label(begin_else);
			transStmt(x.else_branch);
			code.add_label(end);
		}
		else
		{
			Label end = new Label();
			code.add_oper(new Branch(null, jge, end));
			transStmt(x.if_branch);
			code.add_label(end);
		}
	}

	private void transJumpStmt(JumpStmt x) throws Exception
	{
		if (x.category == JumpStmt.jp_brk)
			code.add_oper(new Jump(end_label.peek()));
		else if (x.category == JumpStmt.jp_ctn)
			code.add_oper(new Jump(begin_label.peek()));
		else if (x.category == JumpStmt.jp_ret)
		{
			Temp ret = null;
			if (x.exp != null)
				ret = transExp(x.exp);
			code.add_oper(new Return(ret));
			code.add_oper(new Jump(exit.peek()));
		}
		else
			internal_error();
	}

	private void transIterStmt(IterStmt x) throws Exception
	{
		Label begin = new Label();
		Label end = new Label();

		if (x.category == IterStmt.iter_while)
		{
			begin_label.push(begin);
			end_label.push(end);

			code.add_label(begin);
			Temp jge = transExp(x.judge);
			code.add_oper(new Branch(null, jge, end));
			transStmt(x.stmt);
			code.add_oper(new Jump(begin));
			code.add_label(end);

			begin_label.pop();
			end_label.pop();
		}
		else if (x.category == IterStmt.iter_for)
		{
			begin_label.push(begin);
			end_label.push(end);

			if (x.init != null)
				transExp(x.init);
			code.add_label(begin);
			if (x.judge != null)
			{
				Temp jge = transExp(x.judge);
				code.add_oper(new Branch(null, jge, end));
			}
			transStmt(x.stmt);
			if (x.next != null)
				transExp(x.next);
			code.add_oper(new Jump(begin));
			code.add_label(end);

			begin_label.pop();
			end_label.pop();
		}
		else
			internal_error();
	}

	/* Init */
	private Temp transInit(Init x) throws Exception
	{
		Temp ans = null;
		if (x.listed)
		{
			for (Init it : x.init_list)
				ans = transInit(it);
		}
		else
			ans = transExp(x.exp);

		return ans;
	}

	// Get the address or memory access of a variable
	private Operand getAddr(Exp x) throws Exception
	{
		if (x instanceof PrimaryExp)
		{
			PrimaryExp pe = (PrimaryExp) x;
			if (pe.category == PrimaryExp.pe_id)
			{
				String cid = ((Dec) x.value).name;
				Symbol csym = Symbol.getSymbol(cid);
				Dec cd = (Dec) env.peek().get_global(csym).mirror;
				Const cof = new Const(cd.offset);
				Temp ret = new Temp();
				code.add_oper(new BinOp(Quad.add, global_frame.FP(), cof, ret));
				return ret;
			}
			else if (pe.category == PrimaryExp.pe_paren)
				return getAddr(pe.ce);
			else if (pe.category == PrimaryExp.pe_str)
			{
				// "abcd"[2]
				// TODO
				return izero;
			}
			else
				internal_error();
		}
		else if (x instanceof PostfixExp)
		{
			PostfixExp pfx_exp = (PostfixExp) x;
			Exp base_exp = pfx_exp.pe;
			Operand base_addr = getAddr(base_exp);
			Type ct = base_exp.type;

			ListIterator<PostfixElem> lit = ((PostfixExp) x).elem.listIterator();
			while (lit.hasNext())
			{
				PostfixElem cpfx = lit.next();
				if (cpfx.category == PostfixElem.post_idx)
				{
					if (ct instanceof Array || ct instanceof Pointer)
					{
						Type cur_elem_type = ct instanceof Array ? ((Array) ct).elem_type : ((Pointer) ct).elem_type;
						Const cw = new Const(cur_elem_type.width);
						Temp idx = transExp(cpfx.exp);
						Temp clof = new Temp();
						code.add_oper(new BinOp(Quad.mult, cw, idx, clof));
						code.add_oper(new BinOp(Quad.add, base_addr, clof, base_addr));
					}
					else
						internal_error();
				}
				else if (cpfx.category == PostfixElem.post_dot)
				{
					if (ct instanceof Record)
					{
						int off = ((Record) ct).get_member_offset(cpfx.id);
						Const cof = new Const(off);
						code.add_oper(new BinOp(Quad.add, base_addr, cof, base_addr));
					}
					else
						internal_error();
				}
				else if (cpfx.category == PostfixElem.post_arrow)
				{
					if (ct instanceof Pointer && ((Pointer) ct).elem_type instanceof Record)
					{
						Record crd = (Record) ((Pointer) ct).elem_type;
						int off = crd.get_member_offset(cpfx.id);
						Const cof = new Const(off);
						code.add_oper(new BinOp(Quad.add, base_addr, cof, base_addr));
					}
					else
						internal_error();
				}
				else
					internal_error();
				ct = cpfx.cur_type;
			}
			return base_addr;
		}
		else if (x instanceof UnaryExp)
		{
			UnaryExp ue = (UnaryExp) x;
			if (ue.category == UnaryExp.indirection)
				return transExp(ue.exp);
			else
				internal_error();
		}
		else if (x instanceof CastExp)
			return getAddr(((CastExp) x).exp);
		else
			internal_error();

		return null;
	}

	private Mem getAccess(Exp x) throws Exception
	{
		Mem ret = null;
		if (!x.isLvalue)
			internal_error();

		if (x instanceof PrimaryExp)
		{
			PrimaryExp pe = (PrimaryExp) x;
			if (pe.category == PrimaryExp.pe_id)
			{
				String cid = ((Dec) x.value).name;
				Symbol csym = Symbol.getSymbol(cid);
				Dec cvd = (Dec) env.peek().get_global(csym).mirror;
				int off = cvd.offset;
				ret = new Mem(global_frame.FP(), off);
			}
			else if (pe.category == PrimaryExp.pe_str)
			{
				// TODO
				ret = new Mem(global_frame.FP(), 0);
			}
			else if (pe.category == PrimaryExp.pe_paren)
				ret = getAccess(pe.ce);
			else
				internal_error();
		}
		else if (x instanceof PostfixExp)
			ret = new Mem(getAddr(x));
		else if (x instanceof UnaryExp)
		{
			UnaryExp ue = (UnaryExp) x;
			if (ue.category == UnaryExp.indirection)
				ret = new Mem(getAddr(ue));
			else
				internal_error();
		}
		else
			internal_error();

		return ret;
	}

	private void handleParam(Exp x, Call y) throws Exception
	{
		if (x instanceof CommaExp)
		{
			Iterator<Exp> lit = ((CommaExp) x).exp.iterator();
			while (lit.hasNext())
				handleParam(lit.next(), y);
		}
		else
		{
			Temp ans = transExp(x);
			y.add_param(ans);
		}
	}

	private void panic(String msg) throws Exception
	{
		throw new Exception(msg);
	}

	private void internal_error() throws Exception
	{
		panic("Internal Error.".intern());
	}
}
