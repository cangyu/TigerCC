package compiler.AST;

public abstract class Dec extends ASTNode
{
	public int offset;

	public Dec(int off)
	{
		offset = off;
	}
}
