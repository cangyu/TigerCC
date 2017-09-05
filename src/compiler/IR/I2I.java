package compiler.IR;

public class I2I extends Normal
{
	public I2I(Reg r1, Reg r2)
	{
		super(Operation.i2i, r1, r2);
	}
}
