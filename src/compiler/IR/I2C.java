package compiler.IR;

public class I2C extends Normal
{
	public I2C(Reg r1, Reg r2)
	{
		super(Operation.i2c, new OperandList(r1), new OperandList(r2));
	}
}
