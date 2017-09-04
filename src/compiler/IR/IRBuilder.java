package compiler.IR;

import java.util.*;
import compiler.AST.*;
import compiler.AST.PostfixExp.PostfixElem;
import compiler.Typing.*;

public class IRBuilder
{
	private Prog entrance;
	private ILOCProg tac;
	private Stack<Label> begin_label, end_label;
	private Label exit;

	public IRBuilder(Prog x)
	{
		entrance = x;
		tac = new ILOCProg();
		begin_label = new Stack<Label>();
		end_label = new Stack<Label>();
	}

	public ILOCProg translate() throws Exception
	{
		for (Dec dcl : entrance.general_decl)
		{
			if (dcl instanceof VarDec)
				transVarDec((VarDec) dcl);
			else if (dcl instanceof FuncDec)
				transFuncDec((FuncDec) dcl);
			else
				panic("Internal Error.");
		}
		
		return tac;
	}

	private Reg get_access(Dec x)
	{
		return new Reg(x.offset);
	}

	/* Dec */
	private void transVarDec(VarDec x) throws Exception
	{
		Init ci = x.init;
		if (ci != null)
		{
			Reg dst = get_access(x);
			if (ci.listed)
			{

			}
			else
			{
				Reg init_val = transExp(ci.exp);
				if (ci.exp.type instanceof Int)
					tac.add_oper(new MOVE(init_val, dst));
				else if (ci.exp.type instanceof Char)
				{

				}
			}
		}
	}

	private void transFuncDec(FuncDec x) throws Exception
	{
		// leading label
		Label lf = new Label(x.name);
		tac.add_label(lf);

		// parameters and local variables
		ListIterator<VarDec> vlit = x.var.listIterator();
		while (vlit.hasNext())
			transVarDec(vlit.next());

		// statements
		ListIterator<Stmt> slit = x.st.listIterator();
		while (slit.hasNext())
			transStmt(slit.next());
	}

	/* Exp */
	private Reg transExp(Exp x) throws Exception
	{
		Reg ret = null;

		if (x instanceof CommaExp)
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

	private Reg transCommaExp(CommaExp x) throws Exception
	{
		ListIterator<Exp> alit = x.exp.listIterator();
		Reg ret = null;
		while (alit.hasNext())
			ret = transExp(alit.next());
		return ret;
	}

	private Reg transAssignExp(AssignExp x) throws Exception
	{
		Reg lhs = transExp(x.left);
		Reg rhs = transExp(x.right);

		if (x.assign_type == AssignExp.plain)
			tac.add_oper(new MOVE(rhs, lhs));
		else if (x.assign_type == AssignExp.add)
			tac.add_oper(new ADD(lhs, rhs, lhs));
		else if (x.assign_type == AssignExp.sub)
			tac.add_oper(new SUB(lhs, rhs, lhs));
		else if (x.assign_type == AssignExp.multi)
			tac.add_oper(new MULT(lhs, rhs, lhs));
		else if (x.assign_type == AssignExp.divide)
			tac.add_oper(new DIV(lhs, rhs, lhs));
		else if (x.assign_type == AssignExp.module)
		{
			Reg tmp = new Reg();
			tac.add_oper(new DIV(lhs, rhs, tmp));
			tac.add_oper(new MULT(rhs, tmp, tmp));
			tac.add_oper(new SUB(lhs, tmp, lhs));
		}
		else if (x.assign_type == AssignExp.bit_and)
			tac.add_oper(new AND(lhs, rhs, lhs));
		else if (x.assign_type == AssignExp.bit_or)
			tac.add_oper(new OR(lhs, rhs, lhs));
		else if (x.assign_type == AssignExp.bit_xor)
			tac.add_oper(new XOR(lhs, rhs, lhs));
		else if (x.assign_type == AssignExp.left_shift)
			tac.add_oper(new LSHIFT(lhs, rhs, lhs));
		else if (x.assign_type == AssignExp.right_shift)
			tac.add_oper(new RSHIFT(lhs, rhs, lhs));
		else
			internal_error();

		return lhs;
	}

	private Reg transBinaryExp(BinaryExp x) throws Exception
	{
		Reg lhs = transExp(x.left);
		Reg rhs = transExp(x.right);
		Reg ans = new Reg();

		if (x.op == BinaryExp.multiply)
			tac.add_oper(new MULT(lhs, rhs, ans));
		else if (x.op == BinaryExp.division)
			tac.add_oper(new DIV(lhs, rhs, ans));
		else if (x.op == BinaryExp.module)
		{
			Reg tmp = new Reg();
			tac.add_oper(new DIV(lhs, rhs, ans));
			tac.add_oper(new MULT(rhs, ans, tmp));
			tac.add_oper(new SUB(lhs, tmp, ans));
		}
		else if (x.op == BinaryExp.addition)
			tac.add_oper(new ADD(lhs, rhs, ans));
		else if (x.op == BinaryExp.substraction)
			tac.add_oper(new SUB(lhs, rhs, ans));
		else if (x.op == BinaryExp.bitwise_and)
			tac.add_oper(new AND(lhs, rhs, ans));
		else if (x.op == BinaryExp.bitwise_or)
			tac.add_oper(new OR(lhs, rhs, ans));
		else if (x.op == BinaryExp.bitwise_xor)
			tac.add_oper(new XOR(lhs, rhs, ans));
		else if (x.op == BinaryExp.equal)
			tac.add_oper(new CMP_EQ(lhs, rhs, ans));
		else if (x.op == BinaryExp.greater_equal)
			tac.add_oper(new CMP_GE(lhs, rhs, ans));
		else if (x.op == BinaryExp.greater_than)
			tac.add_oper(new CMP_GT(lhs, rhs, ans));
		else if (x.op == BinaryExp.less_equal)
			tac.add_oper(new CMP_LE(lhs, rhs, ans));
		else if (x.op == BinaryExp.less_than)
			tac.add_oper(new CMP_LT(lhs, rhs, ans));
		else if (x.op == BinaryExp.not_equal)
			tac.add_oper(new CMP_NE(lhs, rhs, ans));
		else if (x.op == BinaryExp.logical_and)
		{
			Reg rz = new Reg();
			Immediate iz = new Immediate(0);
			Label end = new Label();
			Label proceed_2nd = new Label();
			tac.add_oper(new LOADI(iz, rz));
			tac.add_oper(new CMP_NE(lhs, rz, ans));
			tac.add_oper(new CBR(ans, proceed_2nd, end));
			tac.add_label(proceed_2nd);
			tac.add_oper(new CMP_NE(rhs, rz, ans));
			tac.add_label(end);
		}
		else if (x.op == BinaryExp.logical_or)
		{
			Reg rz = new Reg();
			Immediate iz = new Immediate(0);
			Label end = new Label();
			Label proceed_2nd = new Label();
			tac.add_oper(new LOADI(iz, rz));
			tac.add_oper(new CMP_NE(lhs, rz, ans));
			tac.add_oper(new CBR(ans, end, proceed_2nd));
			tac.add_label(proceed_2nd);
			tac.add_oper(new CMP_NE(rhs, rz, ans));
			tac.add_label(end);
		}
		else if (x.op == BinaryExp.shift_left)
			tac.add_oper(new LSHIFT(lhs, rhs, ans));
		else if (x.op == BinaryExp.shift_right)
			tac.add_oper(new RSHIFT(lhs, rhs, ans));
		else
			internal_error();

		return ans;
	}

	private Reg transCastExp(CastExp x) throws Exception
	{
		return null;
	}

	private Reg transUnaryExp(UnaryExp x) throws Exception
	{
		Reg ans = new Reg();
		if (x.category == UnaryExp.address_of)
		{
			Reg tmp = transExp(x.exp);
			int off = tmp.index * 4;
			Immediate iof = new Immediate(off);
			tac.add_oper(new LOADI(iof, ans));
		}
		else if (x.category == UnaryExp.indirection)
		{
			Reg tmp = transExp(x.exp);
			tac.add_oper(new LOAD(tmp, ans));
		}
		else if (x.category == UnaryExp.unary_plus)
		{
			Reg tmp = transExp(x.exp);
			tac.add_oper(new MOVE(tmp, ans));
		}
		else if (x.category == UnaryExp.unary_minus)
		{
			Reg tmp = transExp(x.exp);
			Immediate iz = new Immediate(0);
			tac.add_oper(new RSUBI(tmp, iz, tmp));
			tac.add_oper(new MOVE(tmp, ans));
		}
		else if (x.category == UnaryExp.bitwise_not)
		{
			Reg tmp = transExp(x.exp);
			Immediate iaf = new Immediate(0xFFFFFFFF);
			tac.add_oper(new XORI(tmp, iaf, ans));
		}
		else if (x.category == UnaryExp.logical_negation)
		{
			Reg tmp = transExp(x.exp);
			Immediate iz = new Immediate(0);
			Reg rz = new Reg();
			tac.add_oper(new LOADI(iz, rz));
			tac.add_oper(new CMP_EQ(tmp, rz, ans));
		}
		else if (x.category == UnaryExp.size_of)
		{
			int sz = x.exp != null ? x.exp.type.width : x.type.width;
			Immediate isz = new Immediate(sz);
			tac.add_oper(new LOADI(isz, ans));
		}
		else if (x.category == UnaryExp.inc)
		{
			Reg tmp = transExp(x.exp);
			Immediate ione = new Immediate(1);
			tac.add_oper(new ADDI(tmp, ione, tmp));
			tac.add_oper(new MOVE(tmp, ans));
		}
		else if (x.category == UnaryExp.dec)
		{
			Reg tmp = transExp(x.exp);
			Immediate ione = new Immediate(1);
			tac.add_oper(new SUBI(tmp, ione, tmp));
			tac.add_oper(new MOVE(tmp, ans));
		}
		else
			internal_error();

		return ans;
	}

	private Reg transPfxExp(PostfixExp x) throws Exception
	{
		Reg ans = new Reg();

		Reg base_val = transExp(x.pe);
		ListIterator<PostfixElem> lit = x.elem.listIterator();
		while (lit.hasNext())
		{
			PostfixElem pfx = lit.next();
			if (pfx.category == PostfixElem.post_arrow)
			{

			}
			else if (pfx.category == PostfixElem.post_call)
			{

			}
			else if (pfx.category == PostfixElem.post_dec)
			{

			}
			else if (pfx.category == PostfixElem.post_dot)
			{

			}
			else if (pfx.category == PostfixElem.post_idx)
			{

			}
			else if (pfx.category == PostfixElem.post_inc)
			{

			}
			else
				internal_error();
		}

		return ans;
	}

	private Reg transPrimaryExp(PrimaryExp x) throws Exception
	{
		Reg ans = new Reg();

		if (x.category == PrimaryExp.pe_id)
		{

		}
		else if (x.category == PrimaryExp.pe_ch)
		{

		}
		else if (x.category == PrimaryExp.pe_int)
		{

		}
		else if (x.category == PrimaryExp.pe_fp)
		{

		}
		else if (x.category == PrimaryExp.pe_str)
		{

		}
		else if (x.category == PrimaryExp.pe_paren)
		{
			Reg tmp = transExp(x.ce);
			tac.add_oper(new MOVE(tmp, ans));
		}
		else
			internal_error();

		return ans;
	}

	/* Stmt */
	private void transStmt(Stmt x) throws Exception
	{
		if (x instanceof CompStmt)
			transCompStmt((CompStmt) x);
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
		Reg jge = transExp(x.condition);
		if (x.else_branch != null)
		{
			Label begin_if = new Label();
			Label begin_else = new Label();
			Label end = new Label();
			tac.add_oper(new CBR(jge, begin_if, begin_else));
			tac.add_label(begin_if);
			transStmt(x.if_branch);
			tac.add_oper(new JUMPI(end));
			tac.add_label(begin_else);
			transStmt(x.else_branch);
			tac.add_label(end);
		}
		else
		{
			Label begin_if = new Label();
			Label end = new Label();
			tac.add_oper(new CBR(jge, begin_if, end));
			tac.add_label(begin_if);
			transStmt(x.if_branch);
			tac.add_label(end);
		}
	}

	private void transJumpStmt(JumpStmt x) throws Exception
	{
		if (x.category == JumpStmt.jp_brk)
			tac.add_oper(new JUMPI(end_label.peek()));
		else if (x.category == JumpStmt.jp_ctn)
			tac.add_oper(new JUMPI(begin_label.peek()));
		else if (x.category == JumpStmt.jp_ret)
			tac.add_oper(new JUMPI(exit));
		else
			internal_error();
	}

	private void transIterStmt(IterStmt x) throws Exception
	{	
		Label begin = new Label();
		Label test = new Label();
		Label end = new Label();

		if (x.category == IterStmt.iter_while)
		{
			begin_label.push(begin);
			end_label.push(end);
			
			tac.add_label(begin);
			Reg jge = transExp(x.judge);
			tac.add_oper(new CBR(jge, test, end));
			tac.add_label(test);
			transStmt(x.stmt);
			tac.add_oper(new JUMPI(begin));
			tac.add_label(end);
			
			begin_label.pop();
			end_label.pop();
		}
		else if (x.category == IterStmt.iter_for)
		{
			begin_label.push(begin);
			end_label.push(end);
			
			transExp(x.init);
			tac.add_label(begin);
			Reg jge = transExp(x.judge);
			tac.add_oper(new CBR(jge, test, end));
			transStmt(x.stmt);
			transExp(x.next);
			tac.add_oper(new JUMPI(begin));
			tac.add_label(end);
			
			begin_label.pop();
			end_label.pop();
		}
		else
			internal_error();
	}

	/* Init */
	private Reg transInit(Init x) throws Exception
	{
		return null;
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
