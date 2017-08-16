package compiler.IR;

import java.util.*;
import compiler.AST.*;

public class Translator implements ASTNodeVisitor
{
	private ILOCProg prog;
	private int offset;
	private Stack<Integer> offsets;
	private Stack<Label> breakLabels, continueLabels;

	public Translator()
	{
		prog = null;
		offset = 0;
		breakLabels = new Stack<Label>();
		continueLabels = new Stack<Label>();
	}

	public ILOCProg translate(Prog x) throws Exception
	{
		prog = new ILOCProg();
		visit(x);
		return prog;
	}

	@Override
	public void visit(Prog x) throws Exception
	{
		for (Dec dcl : x.general_decl)
		{
			if (dcl instanceof VarDec)
				visit((VarDec) dcl);
			else if (dcl instanceof FuncDec)
				visit((FuncDec) dcl);
			else
				panic("Internal Error.");
		}
	}

	@Override
	public void visit(VarDec x) throws Exception
	{
		x.operand_list = new LinkedList<Operand>();
		int n = (int) Math.ceil(x.type.width / 4);
		for (int i = 0; i < n; i++)
			x.operand_list.add(new Reg());
	}

	@Override
	public void visit(FuncDec x) throws Exception
	{
		// leading label
		Label lf = new Label(x.name);
		prog.add_instruction(new Instruction(lf, NOP.getInstance()));

		// parameters and local variables
		ListIterator<VarDec> vlit = x.var.listIterator();
		while (vlit.hasNext())
			vlit.next().accept(this);

		// statements
		ListIterator<Stmt> slit = x.st.listIterator();
		while (slit.hasNext())
			slit.next().accept(this);
	}

	@Override
	public void visit(CommaExp x) throws Exception
	{
		ListIterator<AssignExp> alit = x.exp.listIterator();
		while (alit.hasNext())
			alit.next().accept(this);
	}

	@Override
	public void visit(AssignExp x) throws Exception
	{
		x.left.accept(this);
		x.right.accept(this);
	}

	@Override
	public void visit(BinaryExp x) throws Exception
	{
		x.left.accept(this);
		x.right.accept(this);
	}

	@Override
	public void visit(CastExp x) throws Exception
	{
		x.exp.accept(this);
	}

	@Override
	public void visit(UnaryExp x) throws Exception
	{
		if (x.exp != null)
			x.exp.accept(this);
	}

	@Override
	public void visit(PostfixExp x) throws Exception
	{
		x.pe.accept(this);
	}

	@Override
	public void visit(PrimaryExp x) throws Exception
	{
		if (x.ce == null)
			x.ce.accept(this);
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
	public void visit(JumpStmt x) throws Exception
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

	private void panic(String msg) throws Exception
	{
		throw new Exception(msg);
	}
}
