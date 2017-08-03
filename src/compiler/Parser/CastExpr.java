package compiler.Parser;

import java.util.*;

public class CastExpr
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
}
