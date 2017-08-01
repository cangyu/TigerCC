package compiler.Parser;

import compiler.AST.ASTNodeVisitor;
import java.util.*;

public class CastExpr extends Expr
{
	public LinkedList<TypeName> type_list;
	public UnaryExpr expr;

	public CastExpr()
	{
		type_list = new LinkedList<TypeName>();
	}

	public void add_type(TypeName x)
	{
		type_list.add(x);
	}

	public void set_origin(UnaryExpr x)
	{
		expr = x;
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
