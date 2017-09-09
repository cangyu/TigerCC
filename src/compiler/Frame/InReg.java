package compiler.Frame;

import compiler.IR.Operand;
import compiler.IR.Temp;

public class InReg extends Access
{
	public Temp reg;

	public InReg()
	{
		reg = new Temp();
	}

	@Override
	public Operand access()
	{
		return reg;
	}

}
