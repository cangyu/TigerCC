package compiler.Frame;

import java.util.LinkedList;
import compiler.IR.Label;
import compiler.IR.Temp;

public abstract class Frame
{
	public Label name;
	public LinkedList<Access> formal;

	public Frame(Label x)
	{
		name = x;
		formal = new LinkedList<Access>();
	}

	public abstract Temp FP();

	public abstract Temp SP();

	public abstract Temp RA();

	public abstract Temp RV();

	public abstract Access allocLocal(boolean escape);

	public abstract Frame newFrame(Label name);
}
