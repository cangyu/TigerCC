package compiler.IR;

public class C2C extends Normal
{
	public C2C(Reg r1, Reg r2)
	{
		super(Operation.c2c, r1, r2);
	}
}
