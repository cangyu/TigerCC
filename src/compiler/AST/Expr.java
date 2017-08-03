package compiler.AST;

import compiler.Types.*;

public abstract class Expr extends ASTNode
{
	public boolean isConst;
	public Object value;
	public boolean hasInitialized;
	public Type type;
	public boolean isLvalue;
}
