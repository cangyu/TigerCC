package compiler.Frame;

import compiler.IR.Label;

public abstract class Frame
{
	public Label name;

    public abstract Access allocLocal(boolean escape);
}
