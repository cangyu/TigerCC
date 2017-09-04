package compiler.IR;

import java.util.*;
import compiler.AST.*;
import compiler.Typing.*;

public class IRBuilder
{
	private Prog entrance;
	private ILOCProg tac;
	private Stack<Label> brk_lbl, ctn_lbl;

	public IRBuilder(Prog x)
	{
		entrance = x;
		tac = new ILOCProg();
		brk_lbl = new Stack<Label>();
		ctn_lbl = new Stack<Label>();
	}

	public ILOCProg translate() throws Exception
	{
		transProg(entrance);
		return tac;
	}

	private void transProg(Prog x) throws Exception
	{
		for (Dec dcl : x.general_decl)
		{
			if (dcl instanceof VarDec)
				transVarDec((VarDec) dcl);
			else if (dcl instanceof FuncDec)
				transFuncDec((FuncDec) dcl);
			else
				panic("Internal Error.");
		}
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
		tac.add_oper(new MOVE(rhs, lhs));
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
		return null;
	}

	private Reg transPfxExp(PostfixExp x) throws Exception
	{
		return null;
	}

	private Reg transPrimaryExp(PrimaryExp x) throws Exception
	{
		return null;
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
	}

	private void transCompStmt(CompStmt x) throws Exception
	{

	}

	private void transSelectStmt(SelectStmt x) throws Exception
	{

	}

	private void transJumpStmt(JumpStmt x) throws Exception
	{

	}

	private void transIterStmt(IterStmt x) throws Exception
	{
		if (x.category == IterStmt.iter_while)
		{
			Label begin_while = new Label();
			Label end_while = new Label();
			tac.add_label(begin_while);
			Reg cond = new Reg();
		}
		else if (x.category == IterStmt.iter_for)
		{

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
