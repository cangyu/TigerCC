package compiler.AST;

import compiler.Types.*;

public abstract class Exp extends ASTNode
{
	public Type type;
	public boolean isConst;
	public Object value;
	public boolean hasInitialized;
	public boolean isLvalue;
}
