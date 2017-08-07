package compiler.AST;

public class PrimaryExp extends Exp
{
	public CommaExp ce;

	public PrimaryExp()
	{
		super();
		ce = null;
	}

	public void set_expr(CommaExp x)
	{
		// For PrimaryExpr ::= (Expression)
		// Parenthesis will not be discarded as it will be used when performing pretty-print
		ce = x;
	}

	@Override
	public void accept(ASTNodeVisitor v) throws Exception
	{
		v.visit(this);
	}
}
