package compiler.Parser;

import compiler.AST.ASTNodeVisitor;

public class Declaration extends ProgramComp
{
	public TypeSpecifier type_specifier;
	public InitDeclarators init_declarator_list;
	
	public Declaration(TypeSpecifier _ts, InitDeclarators _ids)
	{
		type_specifier = _ts;
		init_declarator_list = _ids;
	}
	
	public void accept(ASTNodeVisitor v) throws Exception
    {
        v.visit(this);
    }
}
