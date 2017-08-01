package compiler.Parser;

import compiler.AST.ASTNodeVisitor;
import java.util.*;

public class AssignmentExpr extends Expr
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
	public BinaryExpr rexpr;

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

	public void set_origin(BinaryExpr y)
	{
		rexpr = y;
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}

	public String getOperator(int op)
	{
		switch (op)
		{
		case ASSIGN:
			return "=".intern();
		case MUL_ASSIGN:
			return "*=".intern();
		case DIV_ASSIGN:
			return "/=".intern();
		case MOD_ASSIGN:
			return "%=".intern();
		case ADD_ASSIGN:
			return "+=".intern();
		case SUB_ASSIGN:
			return "-=".intern();
		case SHL_ASSIGN:
			return "<<=".intern();
		case SHR_ASSIGN:
			return ">>=".intern();
		case AND_ASSIGN:
			return "&=".intern();
		case XOR_ASSIGN:
			return "^=".intern();
		case OR_ASSIGN:
			return "|=".intern();
		default:
			return "".intern();
		}
	}
}
