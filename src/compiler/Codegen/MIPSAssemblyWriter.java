package compiler.Codegen;

import java.util.*;
import compiler.IR.Label;
import compiler.IR.Temp;

public class MIPSAssemblyWriter implements AssemblyWriter
{
	@Override
	public void returnValue(Temp src)
	{
		emit("lw $v0, %d($fp)", src.offset);
	}

	@Override
	public void store(Temp dest, int src)
	{
		emit("li $t0, %d", src);
		emit("sw $t0, %d($fp)", dest.offset);
	}

	@Override
	public void move(Temp dest, Temp src)
	{
		emit("lw $t0, %d($fp)", src.offset);
		emit("sw $t0, %d($fp)", dest.offset);
	}

	@Override
	public void add(Temp dest, Temp src1, Temp src2)
	{
		emit("lw $t1, %d($fp)", src1.offset);
		emit("lw $t2, %d($fp)", src2.offset);
		emit("add $t0, $t1, $t2");
		emit("sw $t0, %d($fp)", dest.offset);
	}

	@Override
	public void sub(Temp dest, Temp src1, Temp src2)
	{
		emit("lw $t1, %d($fp)", src1.offset);
		emit("lw $t2, %d($fp)", src2.offset);
		emit("sub $t0, $t1, $t2");
		emit("sw $t0, %d($fp)", dest.offset);
	}

	@Override
	public void mul(Temp dest, Temp src1, Temp src2)
	{
		emit("lw $t1, %d($fp)", src1.offset);
		emit("lw $t2, %d($fp)", src2.offset);
		emit("mul $t0, $t1, $t2");
		emit("sw $t0, %d($fp)", dest.offset);
	}

	@Override
	public void div(Temp dest, Temp src1, Temp src2)
	{
		emit("lw $t1, %d($fp)", src1.offset);
		emit("lw $t2, %d($fp)", src2.offset);
		emit("div $t0, $t1, $t2");
		emit("sw $t0, %d($fp)", dest.offset);
	}

	@Override
	public void seq(Temp dest, Temp src1, Temp src2)
	{
		emit("lw $t1, %d($fp)", src1.offset);
		emit("lw $t2, %d($fp)", src2.offset);
		emit("seq $t0, $t1, $t2");
		emit("sw $t0, %d($fp)", dest.offset);
	}

	@Override
	public void sne(Temp dest, Temp src1, Temp src2)
	{
		emit("lw $t1, %d($fp)", src1.offset);
		emit("lw $t2, %d($fp)", src2.offset);
		emit("sne $t0, $t1, $t2");
		emit("sw $t0, %d($fp)", dest.offset);
	}

	@Override
	public void slt(Temp dest, Temp src1, Temp src2)
	{
		emit("lw $t1, %d($fp)", src1.offset);
		emit("lw $t2, %d($fp)", src2.offset);
		emit("slt $t0, $t1, $t2");
		emit("sw $t0, %d($fp)", dest.offset);
	}

	@Override
	public void sgt(Temp dest, Temp src1, Temp src2)
	{
		emit("lw $t1, %d($fp)", src1.offset);
		emit("lw $t2, %d($fp)", src2.offset);
		emit("sgt $t0, $t1, $t2");
		emit("sw $t0, %d($fp)", dest.offset);
	}

	@Override
	public void sle(Temp dest, Temp src1, Temp src2)
	{
		emit("lw $t1, %d($fp)", src1.offset);
		emit("lw $t2, %d($fp)", src2.offset);
		emit("sle $t0, $t1, $t2");
		emit("sw $t0, %d($fp)", dest.offset);
	}

	@Override
	public void sge(Temp dest, Temp src1, Temp src2)
	{
		emit("lw $t1, %d($fp)", src1.offset);
		emit("lw $t2, %d($fp)", src2.offset);
		emit("sge $t0, $t1, $t2");
		emit("sw $t0, %d($fp)", dest.offset);
	}

	@Override
	public void emit(Label label)
	{
		emit(label.toString() + ":");
	}

	@Override
	public void jump(Label label)
	{
		emit("j %s", label.toString());
	}

	@Override
	public void jz(Temp cond, Label label)
	{
		emit("lw $t0, %d($fp)", cond.offset);
		emit("beqz $t0, %s", label.toString());
	}

	private List<String> lines = new ArrayList<String>();

	private void emit(String fmt, Object... objects)
	{
		lines.add(String.format(fmt, objects));
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (String line : lines)
		{
			if (!line.startsWith(".") && !line.endsWith(":"))
			{
				sb.append('\t');
			}
			sb.append(line);
			sb.append('\n');
		}
		return sb.toString();
	}

	@Override
	public void emitPrologue()
	{
		emit(".text");
		emit(".align 2");
		emit(".globl main");
		emit("main:");
		emit("addi $sp, $sp, -4 # move up a cell");
		emit("move $fp, $sp     # start using memory here");
		emit("move $gp, $sp     # set global pointer (unused)");
	}

	@Override
	public void emitEpilogue()
	{
		emit("move $a0, $v0");
		emit("li $v0, 1         # print_int");
		emit("syscall");
		emit("li $v0, 10        # exit");
		emit("syscall");
	}
}
