package compiler.Frame;

import compiler.IR.*;

public class MIPSFrame extends Frame
{
	public Temp fp, sp, ra, zero;
	public Temp[] v, a, t, s;
	public int alloc_down;

	public MIPSFrame(Label x)
	{
		super(x);
		fp = new Temp("fp");
		sp = new Temp("sp");
		ra = new Temp("ra");
		zero = new Temp("zero");

		v = new Temp[2];
		for (int i = 0; i < 2; i++)
			v[i] = new Temp("v" + i);

		a = new Temp[4];
		for (int i = 0; i < 4; i++)
			a[i] = new Temp("a" + i);

		s = new Temp[8];
		for (int i = 0; i < 8; i++)
			s[i] = new Temp("s" + i);

		t = new Temp[10];
		for (int i = 0; i < 10; i++)
			t[i] = new Temp("t" + i);

		alloc_down = 0;
	}

	@Override
	public Access allocLocal(boolean escape)
	{
		Access ret = null;

		if (escape)
		{
			ret = new InFrame(this, alloc_down);
			alloc_down -= 4;
		}
		else
			ret = new InReg();

		return ret;
	}

	@Override
	public Temp FP()
	{
		return fp;
	}

	@Override
	public Temp SP()
	{
		return sp;
	}

	@Override
	public Temp RA()
	{
		return ra;
	}

	@Override
	public Temp RV()
	{
		return v[0];
	}

	@Override
	public Frame newFrame(Label name)
	{
		return new MIPSFrame(name);
	}

}
