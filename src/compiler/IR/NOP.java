package compiler.IR;

public class NOP extends Normal
{
	private NOP()
	{
		super(Opcode.nop, null, null);
	}

	private static NOP instance;

	public static NOP getInstance()
	{
		if (instance == null)
			instance = new NOP();

		return instance;
	}
}
