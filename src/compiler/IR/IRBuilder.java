package compiler.IR;

import java.util.*;
import compiler.AST.*;

public class IRBuilder
{
	private Prog entrance;
	private ILOCProg tac_ir;
	private Stack<Label> breakLabels, continueLabels;

	public IRBuilder(Prog x)
	{
		entrance = x;
		tac_ir = new ILOCProg();
		breakLabels = new Stack<Label>();
		continueLabels = new Stack<Label>();
	}

	public ILOCProg translate() throws Exception
	{
		transProg(entrance);
		return tac_ir;
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

	/* Dec */
	private void transVarDec(VarDec x) throws Exception
	{
		return;
	}

	private void transFuncDec(FuncDec x) throws Exception
	{
		// leading label
		Label lf = new Label(x.name);
		tac_ir.add_instruction(new Instruction(lf, NOP.getInstance()));

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
		tac_ir.add_instruction(new Instruction(new I2I(rhs, lhs)));
		return lhs;
	}

	private Reg transBinaryExp(BinaryExp x) throws Exception
	{
		Reg lhs = transExp(x.left);
		Reg rhs = transExp(x.right);
		Reg ans = null;
		
		if (x.op == BinaryExp.addition)
		{
			
		}
		else if (x.op == BinaryExp.bitwise_and)
		{

		}
		else if (x.op == BinaryExp.bitwise_or)
		{

		}
		else if (x.op == BinaryExp.bitwise_xor)
		{

		}
		else if (x.op == BinaryExp.division)
		{

		}
		else if (x.op == BinaryExp.equal)
		{

		}
		else if (x.op == BinaryExp.greater_equal)
		{

		}
		else if (x.op == BinaryExp.greater_than)
		{

		}
		else if (x.op == BinaryExp.less_equal)
		{

		}
		else if (x.op == BinaryExp.less_than)
		{

		}
		else if (x.op == BinaryExp.logical_and)
		{

		}
		else if (x.op == BinaryExp.logical_or)
		{

		}
		else if (x.op == BinaryExp.module)
		{

		}
		else if (x.op == BinaryExp.multiply)
		{

		}
		else if (x.op == BinaryExp.not_equal)
		{

		}
		else if (x.op == BinaryExp.shift_left)
		{

		}
		else if (x.op == BinaryExp.shift_right)
		{

		}
		else if (x.op == BinaryExp.substraction)
		{

		}
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
		if (x.exp != null)
			x.exp.accept(this);
	}

	private Reg transPfxExp(PostfixExp x) throws Exception
	{
		x.pe.accept(this);
	}

	private Reg transPrimaryExp(PrimaryExp x) throws Exception
	{
		if (x.ce == null)
			x.ce.accept(this);
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
			tac_ir.add_instruction(new Instruction(begin_while));
			Reg cond = new Reg();
			x.judge.accept(this);

		}
		else if (x.category == IterStmt.iter_for)
		{

		}
		else
			internal_error();
	}

	/* Init */
	private Operand transInit(Init x) throws Exception
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
