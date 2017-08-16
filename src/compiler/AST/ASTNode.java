package compiler.AST;

import java.util.LinkedList;
import compiler.IR.Operand;

public abstract class ASTNode
{
	public String[] ast_rep;
	public LinkedList<Operand> operand_list;

	public abstract void accept(ASTNodeVisitor v) throws Exception;

	public String toString()
	{
		String ret = "".intern();

		if (ast_rep != null)
			for (int i = 0; i < ast_rep.length; i++)
				ret += ast_rep[i] + "\n".intern();

		return ret;
	}
}
