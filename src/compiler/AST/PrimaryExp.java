package compiler.AST;

public class PrimaryExp extends Exp
{
	public static final int pe_id = 0;
	public static final int pe_int = 1;
	public static final int pe_ch = 2;
	public static final int pe_fp = 3;
	public static final int pe_str = 4;
	public static final int pe_paren = 5;

	public int category;
	public Exp ce;

	public PrimaryExp(int ct)
	{
		super();
		category = ct;
		ce = null;
	}

	public PrimaryExp(Exp x)
	{
		// For PrimaryExpr ::= (Expression)
		// Parenthesis will not be discarded as it will be used when performing pretty-print
		super();
		category = pe_paren;
		ce = x;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
