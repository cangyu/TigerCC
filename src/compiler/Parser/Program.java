package compiler.Parser;

import java.util.*;

public class Program
{
	public LinkedList<ProgComp> elem;

	public Program()
	{
		elem = new LinkedList<ProgComp>();
	}

	public void add_elem(ProgComp x)
	{
		elem.add(x);
	}
}
