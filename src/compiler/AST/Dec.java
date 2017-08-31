package compiler.AST;

public abstract class Dec extends ASTNode
{
	public int offset;
	public String name;

	public Dec(int off, String nm)
	{
		offset = off;
		name = nm;
	}
}
