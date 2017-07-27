package compiler.Parser;

import java.util.*;

import compiler.AST.ASTNode;
import compiler.AST.ASTNodeVisitor;

public class TypeSpecifier extends ASTNode
{
	public final static int ts_void = 0;
	public final static int ts_int = 1;
	public final static int ts_char = 2;
	public final static int ts_float = 3;
	public final static int ts_struct = 4;
	public final static int ts_union = 5;

	public final static TypeSpecifier TS_VOID = new TypeSpecifier(ts_void);
	public final static TypeSpecifier TS_INT = new TypeSpecifier(ts_int);
	public final static TypeSpecifier TS_CHAR = new TypeSpecifier(ts_char);
	public final static TypeSpecifier TS_FLOAT = new TypeSpecifier(ts_float);

	public int ts_type;
	public String name;
	public LinkedList<RecordEntry> entry;

	public TypeSpecifier(int t)
	{
		ts_type = t;
		if (t == ts_struct || t == ts_union)
			entry = new LinkedList<RecordEntry>();
	}

	public TypeSpecifier(int t, String n)
	{
		ts_type = t;
		name = n;
		if (t == ts_struct || t == ts_union)
			entry = new LinkedList<RecordEntry>();
	}

	public void add_entry(RecordEntry x)
	{
		entry.add(x);
	}

	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
