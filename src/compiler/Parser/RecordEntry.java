package compiler.Parser;

import java.util.*;

public class RecordEntry
{
	public TypeSpecifier ts;
	public LinkedList<Declarator> dls;

	public RecordEntry(TypeSpecifier x)
	{
		ts = x;
		dls = new LinkedList<Declarator>();
	}

	public void add_elem(Declarator x)
	{
		dls.add(x);
	}
}
