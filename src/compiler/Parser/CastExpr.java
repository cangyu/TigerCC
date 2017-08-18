package compiler.Parser;

import java.util.*;

public class CastExpr extends Expr
{
	public LinkedList<TypeName> type_list;
	public Expr expr;

	public CastExpr()
	{
		type_list = new LinkedList<TypeName>();
		expr = null;
	}

	public void add_type(TypeName x)
	{
		type_list.add(x);
	}

	public void set_origin(Expr x)
	{
		expr = x;
	}
}
