package compiler.AST;

import compiler.Parser.ArgumentList;
import compiler.Parser.AssignmentExpr;
import compiler.Parser.BinaryExpr;
import compiler.Parser.CastExpr;
import compiler.Parser.CompoundStmt;
import compiler.Parser.Constant;
import compiler.Parser.Declaration;
import compiler.Parser.DeclarationList;
import compiler.Parser.Declarator;
import compiler.Parser.DeclaratorList;
import compiler.Parser.Expression;
import compiler.Parser.ExpressionStmt;
import compiler.Parser.FuncDef;
import compiler.Parser.InitDeclarator;
import compiler.Parser.InitDeclaratorList;
import compiler.Parser.Initializer;
import compiler.Parser.InitializerList;
import compiler.Parser.IterationStmt;
import compiler.Parser.JumpStmt;
import compiler.Parser.NonInitDeclaration;
import compiler.Parser.NonInitDeclarationList;
import compiler.Parser.ParameterList;
import compiler.Parser.PlainDeclaration;
import compiler.Parser.PlainDeclarator;
import compiler.Parser.PostfixExpr;
import compiler.Parser.PrimaryExpr;
import compiler.Parser.Program;
import compiler.Parser.RecordEntry;
import compiler.Parser.SelectionStmt;
import compiler.Parser.StarList;
import compiler.Parser.StmtList;
import compiler.Parser.TypeName;
import compiler.Parser.TypeSpecifier;
import compiler.Parser.UnaryExpr;

public interface ASTNodeVisitor
{	
    /* Expr */
    public abstract void visit(Expression x) throws Exception;
    public abstract void visit(AssignmentExpr x) throws Exception;
    public abstract void visit(BinaryExpr x) throws Exception;
    public abstract void visit(CastExpr x) throws Exception;
    public abstract void visit(UnaryExpr x) throws Exception;
    public abstract void visit(PostfixExpr x) throws Exception;
    public abstract void visit(PrimaryExpr x) throws Exception;
    
    /* Stmt */
    public abstract void visit(StmtList x) throws Exception;
    public abstract void visit(ExpressionStmt x) throws Exception;
    public abstract void visit(CompoundStmt x) throws Exception;
    public abstract void visit(SelectionStmt x) throws Exception;
    public abstract void visit(JumpStmt x) throws Exception;
    public abstract void visit(IterationStmt x) throws Exception;
    
    /* Decl */
    public abstract void visit(StarList x) throws Exception;
    public abstract void visit(Declaration x) throws Exception;
    public abstract void visit(Declarator x) throws Exception;
    public abstract void visit(DeclarationList x) throws Exception;
    public abstract void visit(DeclaratorList x) throws Exception;
    public abstract void visit(InitDeclarator x) throws Exception;
    public abstract void visit(InitDeclaratorList x) throws Exception;
    public abstract void visit(Initializer x) throws Exception;
    public abstract void visit(InitializerList x) throws Exception;
    public abstract void visit(NonInitDeclaration x) throws Exception;
    public abstract void visit(NonInitDeclarationList x) throws Exception;
    public abstract void visit(PlainDeclaration x) throws Exception;
    public abstract void visit(PlainDeclarator x) throws Exception;

    /* Func */
    public abstract void visit(FuncDef x) throws Exception;
    public abstract void visit(ArgumentList x) throws Exception;
    public abstract void visit(ParameterList x) throws Exception;

    /* Type */
    public abstract void visit(TypeName x) throws Exception;
    public abstract void visit(TypeSpecifier x) throws Exception;
    
    /* Program */
    public abstract void visit(Program x) throws Exception;
	public abstract void visit(RecordEntry recordEntry);
	public abstract void visit(Constant constant);
}
