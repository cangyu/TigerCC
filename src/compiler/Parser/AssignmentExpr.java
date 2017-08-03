package compiler.Parser;

import java.util.*;

import compiler.AST.BinaryExpr;

public class AssignmentExpr
{
	public static final int ASSIGN = 0;
	public static final int MUL_ASSIGN = 1;
	public static final int DIV_ASSIGN = 2;
	public static final int MOD_ASSIGN = 3;
	public static final int ADD_ASSIGN = 4;
	public static final int SUB_ASSIGN = 5;
	public static final int SHL_ASSIGN = 6;
	public static final int SHR_ASSIGN = 7;
	public static final int AND_ASSIGN = 8;
	public static final int XOR_ASSIGN = 9;
	public static final int OR_ASSIGN = 10;

	public LinkedList<Integer> op_list;
	public LinkedList<UnaryExpr> lexpr_list;
	public LogicalOrExpr rexpr;

	public AssignmentExpr()
	{
		op_list = new LinkedList<Integer>();
		lexpr_list = new LinkedList<UnaryExpr>();
	}

	public void add_left_expr(UnaryExpr x, int op)
	{
		lexpr_list.add(x);
		op_list.add(new Integer(op));
	}

	public void set_origin(LogicalOrExpr y)
	{
		rexpr = y;
	}
}
