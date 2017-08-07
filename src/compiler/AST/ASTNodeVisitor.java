package compiler.AST;

public interface ASTNodeVisitor
{
	/* exp */
	public abstract void visit(CommaExp x) throws Exception;

	public abstract void visit(AssignExp x) throws Exception;

	public abstract void visit(BinaryExp x) throws Exception;

	public abstract void visit(CastExp x) throws Exception;

	public abstract void visit(UnaryExp x) throws Exception;

	public abstract void visit(PostfixExp x) throws Exception;

	public abstract void visit(PrimaryExp x) throws Exception;

	/* stmt */
	public abstract void visit(ExprStmt x) throws Exception;

	public abstract void visit(CompStmt x) throws Exception;

	public abstract void visit(SelectStmt x) throws Exception;

	public abstract void visit(JumpStmt x) throws Exception;

	public abstract void visit(IterStmt x) throws Exception;

	/* dec */
	public abstract void visit(VarDec x) throws Exception;

	public abstract void visit(FuncDec x) throws Exception;

	/* init */
	public abstract void visit(Init x) throws Exception;

	/* prog */
	public abstract void visit(Prog x) throws Exception;
}
